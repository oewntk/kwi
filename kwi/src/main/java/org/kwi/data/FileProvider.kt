package org.kwi.data

import org.kwi.Config
import org.kwi.data.IHasLifecycle.ObjectClosedException
import org.kwi.item.IHasVersion
import org.kwi.item.POS
import org.kwi.item.Synset
import org.kwi.item.Synset.Companion.zeroFillOffset
import org.kwi.item.Version
import org.kwi.item.Version.Companion.NO_VERSION
import java.io.File
import java.io.FileFilter
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URI
import java.net.URL
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * Manage access to data source objects.
 * Before the provider can be used, a client must call setSource (or call the appropriate constructor) followed by open.
 * Otherwise, the provider will throw an exception.
 *
 * Implementation of a data provider for Wordnet that uses files in the file system to back instances of its data sources.
 * This implementation takes a URL to a file system directory as its path argument,
 * and uses the resource hints from the data types and parts of speech for its content types to examine the filenames in the that directory to determine which files contain which data.
 *
 * This implementation supports loading the Wordnet files into memory, but this is actually not that beneficial for speed.
 * This is because the implementation loads the file data into memory uninterpreted, and on modern machines, the time to interpret a line of data (i.e., parse it into a Java object) is much larger than the time it takes to load the line from disk.
 * Those wishing to achieve speed increases from loading Wordnet into memory should rely on the implementation in RAMDictionary, or something similar, which pre-processes the Wordnet data into objects before caching them.
 */
class FileProvider @JvmOverloads constructor(
    url: URL,
    config: Config? = null,
    loadPolicy: Int = LoadPolicy.NO_LOAD,
    contentTypes: Collection<ContentType<*>> = ContentType.values(),
) : IHasVersion, IHasLifecycle, IHasCharset, ILoadable {

    // D A T A   L O C A T I O N

    /**
     * The location of the data.
     * The source URL from which the provider accesses the data from which it instantiates data sources.
     * The data at the specified location may be in an implementation-specific format.
     * If the provider is currently open, this method throws an `IllegalStateException`.
     */
    var url: URL = url
        set(url) {
            check(!isOpen) { "provider currently open" }
            field = url
        }

    // S Y N C   L O C K S

    private val lifecycleLock: Lock = ReentrantLock()

    private val loadingLock: Lock = ReentrantLock()

    // C O N T E N T   T Y P E S

    private val contentTypesByKey: Map<ContentTypeKey, ContentType<*>> = contentTypes
        .asSequence()
        .map { it.key to it }
        .toMap()

    private var fileMap: Map<ContentType<*>, ILoadableDataSource<*>>? = null

    // C H A R S E T

    /**
     * The character set associated with this dictionary.
     *
     * @param charset the possibly-null character set to use when decoding files.
     * @throws IllegalStateException if the provider is currently open
     */
    override var charset: Charset? = config?.charSet ?: Charsets.UTF_8

    // V E R S I O N

    /**
     * Version
     */
    override var version: Version? = null
        get() {
            checkOpen()
            if (field == null) {
                field = determineVersion(fileMap!!.values)
            }
            if (field === NO_VERSION) {
                return null
            }
            return field
        }

    // L O A D I N G

    @Transient
    private var loader: KWIBackgroundLoader? = null

    var loadPolicy: Int = loadPolicy
        set(policy) {
            try {
                loadingLock.lock()
                field = policy
            } finally {
                loadingLock.unlock()
            }
        }

    /**
     * Constructs the file provider pointing to the resource indicated by the path.
     * This file provider has an initial NO_LOAD load policy.
     *
     * @param file A file pointing to the Wordnet directory
     */
    constructor(file: File) : this(toURL(file))

    init {
        require(!contentTypes.isEmpty())
    }

    /**
     * Returns the first content type, if any, that matches the specified data type and pos object.
     *
     * @param <T> type
     * @param dataType the data type, possibly null, of the desired content type
     * @param pos the part-of-speech, possibly null, of the desired content type
     * @return the first content type that matches the specified data type and part-of-speech.
     */
    fun <T> resolveContentType(dataType: DataType<T>, pos: POS?): ContentType<T> {
        @Suppress("UNCHECKED_CAST")
        return contentTypesByKey.entries
            .first { (k, _) -> k.dataType == dataType && k.pOS == pos }.value as ContentType<T>
    }

    @Throws(IOException::class)
    override fun open(): Boolean {
        try {
            lifecycleLock.lock()
            loadingLock.lock()

            // make sure directory exists
            val directory: File = toFile(url)
            if (!directory.exists()) {
                throw IOException("Dictionary directory does not exist: $directory")
            }

            // get files in directory
            val files = directory.listFiles(FileFilter { obj: File -> obj.isFile })
            if (files == null || files.size == 0) {
                throw IOException("No files found in $directory")
            }

            // make the source map
            fileMap = createSourceMap(files, loadPolicy)

            // do load
            try {
                when (loadPolicy) {
                    LoadPolicy.BACKGROUND_LOAD -> load(false)
                    LoadPolicy.IMMEDIATE_LOAD  -> load(true)
                    else                       -> {}
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            return true
        } finally {
            lifecycleLock.unlock()
            loadingLock.unlock()
        }
    }

    override fun load() {
        try {
            load(false)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    @Throws(InterruptedException::class)
    override fun load(block: Boolean) {
        try {
            loadingLock.lock()
            checkOpen()
            if (isLoaded) {
                return
            }
            if (loader != null) {
                return
            }
            loader = KWIBackgroundLoader()
            loader!!.start()
            if (block) {
                loader!!.join()
            }
        } finally {
            loadingLock.lock()
        }
    }

    override val isLoaded: Boolean
        get() {
            check(isOpen) { "provider not open" }
            try {
                loadingLock.lock()
                for (source in fileMap!!.values) {
                    if (!source.isLoaded) {
                        return false
                    }
                }
                return true
            } finally {
                loadingLock.unlock()
            }
        }

    /**
     * Returns a data source object for the specified content type, if one is available; otherwise returns null.
     *
     * @param <T> the content type of the data source
     * @param contentType the content type of the data source to be retrieved
     * @return the data source for the specified content type, or null if this provider has no such data source
     * @throws ObjectClosedException if the provider is not open when this call is made
     */
    fun <T> getSource(contentType: ContentType<T>): ILoadableDataSource<T>? {
        checkOpen()

        // assume at first this the prototype
        var actualType = contentTypesByKey[contentType.key]

        // if this does not map to an adjusted type, we will check under it directly
        if (actualType == null) {
            actualType = contentType
        }
        @Suppress("UNCHECKED_CAST")
        return fileMap!![actualType] as ILoadableDataSource<T>
    }

    /**
     * Creates the map that contains the content types mapped to the data sources.
     * The method should return a non-null result, but it may be empty if no data sources can be created.
     *
     * @param files0 the files from which the data sources should be created
     * @param policy the load policy of the provider
     * @return a map, possibly empty, of content types mapped to data sources
     * @throws IOException if there is a problem creating the data source
     */
    @Throws(IOException::class)
    private fun createSourceMap(files0: Array<File>, policy: Int): Map<ContentType<*>, ILoadableDataSource<*>> {

        // sort them
        var files = files0
            .sortedBy { it.name }
            .toMutableList()

        return contentTypesByKey.values
            .map { contentType: ContentType<*> ->
                var file = contentType.dataType.find(contentType.pOS, files)
                contentType to file
            }
            .filter { (_, file) -> file != null }
            .onEach { (contentType, file) ->
                // do not remove file from possible choices as both content types may use the same file
                if ((contentType.key != ContentTypeKey.SENSE) &&
                    (contentType.key != ContentTypeKey.INDEX_ADJECTIVE) &&
                    (contentType.key != ContentTypeKey.INDEX_ADVERB) &&
                    (contentType.key != ContentTypeKey.INDEX_NOUN) &&
                    (contentType.key != ContentTypeKey.INDEX_VERB)
                ) {
                    files.remove(file)
                }
            }
            .associate { (contentType, file) -> contentType to createDataSource(file!!, contentType, policy) }
    }

    /**
     * Creates the actual data source implementations.
     *
     * @param <T> the content type of the data source
     * @param file the file from which the data source should be created
     * @param contentType the content type of the data source
     * @param policy the load policy to follow when creating the data source
     * @return the created data source
     * @throws IOException if there is an IO problem when creating the data source
     */
    @Throws(IOException::class)
    private fun <T> createDataSource(file: File, contentType: ContentType<T>, policy: Int): ILoadableDataSource<T> {

        if (contentType.dataType === DataType.DATA) {

            val daSrc: DirectAccessWordnetFile<T> = prepareSource(createDirectAccess<T>(file, contentType, charset), policy) as DirectAccessWordnetFile<T>

            // check to see if direct access works with the file
            // often people will extract the files incorrectly on Windows machines and the binary files will be corrupted with extra CRs
            if (testDirectAccess(daSrc, contentType)) {
                return daSrc
            }

            System.err.println("Error on direct access in ${contentType.pOS} data file: check CR/LF endings")
            // fallback
        }

        return prepareSource(createBinarySearch<T>(file, contentType, charset), policy)
    }

    private fun <T> prepareSource(src: ILoadableDataSource<T>, policy: Int): ILoadableDataSource<T> {
        src.open()
        if (policy == LoadPolicy.IMMEDIATE_LOAD) {
            try {
                src.load(true)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        return src
    }

    private fun <T> testDirectAccess(daSrc: DirectAccessWordnetFile<T>, contentType: ContentType<*>): Boolean {
        // get first line and try to find line by direct access
        // get first line
        val firstLine = daSrc.iterator().next()

        // extract key
        val synset = contentType.dataType.parser.parseLine(firstLine) as Synset
        val key = zeroFillOffset(synset.offset)

        // try to find line by direct access
        val soughtLine = daSrc.getLine(key)
        return soughtLine != null
    }

    /**
     * Creates a direct access data source for the specified type, using the specified file.
     *
     * @param <T> the parameter of the content type
     * @param file the file on which the data source is based
     * @param contentType the data type for the data source
     * @param charset the character sset used for decoding
     * @return the data source
     */
    private fun <T> createDirectAccess(file: File, contentType: ContentType<T>, charset: Charset?): DirectAccessWordnetFile<T> {
        return DirectAccessWordnetFile<T>(file, contentType, charset)
    }

    /**
     * Creates a binary search data source for the specified type, using the specified file.
     *
     * @param <T> the parameter of the content type
     * @param file the file on which the data source is based
     * @param contentType the data type for the data source
     * @param charset the character sset used for decoding
     * @return the data source
     */
    private fun <T> createBinarySearch(file: File, contentType: ContentType<T>, charset: Charset?): ILoadableDataSource<T> {
        //TODO HACK
        return if ("Word" == contentType.dataType.toString()) BinaryStartSearchWordnetFile<T>(file, contentType, charset) else BinarySearchWordnetFile<T>(file, contentType, charset)
    }

    override val isOpen: Boolean
        get() {
            try {
                lifecycleLock.lock()
                return fileMap != null
            } finally {
                lifecycleLock.unlock()
            }
        }

    override fun close() {
        try {
            lifecycleLock.lock()
            if (!isOpen) {
                return
            }
            if (loader != null) {
                loader!!.cancel()
            }
            for (source in fileMap!!.values) {
                source.close()
            }
            fileMap = null
        } finally {
            lifecycleLock.unlock()
        }
    }

    /**
     * Convenience method that throws an exception if the provider is closed.
     *
     * @throws ObjectClosedException if the provider is closed
     */
    private fun checkOpen() {
        if (!isOpen) {
            throw ObjectClosedException()
        }
    }

    /**
     * A thread class which tries to load each data source in this provider.
     */
    private inner class KWIBackgroundLoader : Thread() {

        @Transient
        private var cancel = false

        /**
         * Constructs a new background loader that operates on the internal data structures of this provider.
         */
        init {
            name = KWIBackgroundLoader::class.java.simpleName
            isDaemon = true
        }

        override fun run() {
            try {
                for (source in fileMap!!.values) {
                    if (!cancel && !source.isLoaded) {
                        try {
                            source.load(true)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                }
            } finally {
                loader = null
            }
        }

        /**
         * Sets the cancel flag for this loader.
         */
        fun cancel() {
            cancel = true
            try {
                join()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Determines a version from the set of data sources, if possible, otherwise returns NO_VERSION
     *
     * @param srcs the data sources to be used to determine the version
     * @return the single version that describes these data sources, or NO_VERSION if there is none
     */
    private fun determineVersion(srcs: Collection<IDataSource<*>>): Version? {

        val versionedSources = srcs.filter { it.version != null }
        val version1 = versionedSources.firstOrNull()?.version
        return if (version1 != null && versionedSources.all { version1 == it.version })
            version1 else null
    }

    companion object {

        /**
         * Transforms a URL into a File. The URL must use the 'file' protocol and must be in a UTF-8 compatible format as specified in URLDecoder.
         *
         * @param url url
         * @return a file pointing to the same place as the url
         * @throws IllegalArgumentException if the url does not use the 'file' protocol
         */
        fun toFile(url: URL): File {
            require(url.protocol == "file") { "URL source must use 'file' protocol" }
            try {
                return File(URLDecoder.decode(url.path, "UTF-8"))
            } catch (e: UnsupportedEncodingException) {
                throw RuntimeException(e)
            }
        }

        /**
         * Transforms a file into a URL.
         *
         * @param file the file to be transformed
         * @return a URL representing the file
         */
        fun toURL(file: File): URL {
            val uri = URI("file", "//", file.toURI().toURL().path, null)
            return URL("file", null, uri.rawPath)
        }

        /**
         * A utility method for checking whether a file represents an existing local directory.
         *
         * @param url the url object to check
         * @return whether url object represents a local directory which exists
         */
        fun isLocalDirectory(url: URL): Boolean {
            if (url.protocol != "file") {
                return false
            }
            val file: File = toFile(url)
            return isLocalDirectory(file)
        }

        /**
         * A utility method for checking whether a file represents an existing local directory.
         *
         * @param dir the file object to check
         * @return whether the file object represents a local directory which exist
         */
        fun isLocalDirectory(dir: File): Boolean {
            return dir.exists() && dir.isDirectory
        }

        /**
         * A utility method for checking whether a file represents an existing local file.
         *
         * @param url the url object to check
         * @return whether the url object represents a local file which exists
         */
        fun isLocalFile(url: URL): Boolean {
            if (url.protocol != "file") {
                return false
            }
            val file: File = toFile(url)
            return isLocalFile(file)
        }

        /**
         * A utility method for checking whether a file represents an existing local file
         *
         * @param file the file object to check
         * @return whether the file object represents a local file which exist
         */
        fun isLocalFile(file: File): Boolean {
            return file.exists() && file.isFile
        }
    }
}
