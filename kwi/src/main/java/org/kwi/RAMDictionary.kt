package org.kwi

import org.kwi.data.FileProvider
import org.kwi.data.IHasLifecycle.LifecycleState
import org.kwi.data.IHasLifecycle.ObjectOpenException
import org.kwi.data.LoadPolicy.BACKGROUND_LOAD
import org.kwi.data.LoadPolicy.IMMEDIATE_LOAD
import org.kwi.item.ExceptionEntry
import org.kwi.item.ExceptionKey
import org.kwi.item.Index
import org.kwi.item.IndexID
import org.kwi.item.POS
import org.kwi.item.SenseEntry
import org.kwi.item.SenseID
import org.kwi.item.SenseKey
import org.kwi.item.Synset
import org.kwi.item.SynsetID
import org.kwi.item.Version
import java.io.File
import java.net.URL
import java.util.concurrent.Callable

/**
 * Dictionary that wraps an arbitrary dictionary object.
 * @property backingDictionary dictionary that backs this dictionary
 * @property loadPolicy load policy
 * @param loadPolicy load policy
 * @param config configuration bundle
 */
class RAMDictionary
@JvmOverloads
constructor(
    /**
     * The dictionary that backs this instance
     */
    val backingDictionary: IDictionary,

    /**
     * Load policy
     */
    loadPolicy: Int,

    /**
     * Configuration bundle
     */
    config: Config? = null,

    ) : BaseRAMDictionary() {

    var loadPolicy: Int = loadPolicy
        set(policy) {
            if (isOpen)
                throw ObjectOpenException()
            loadPolicy = policy
        }

    override val version: Version?
        get() = backingDictionary.version

    /**
     * Loads data from the specified File using the specified load policy.
     *
     * Constructs a new wrapper RAM dictionary that will load the contents the specified local Wordnet data, with the specified load policy.
     *
     * @param file a file pointing to a local copy of Wordnet
     * @param loadPolicy the load policy of the dictionary
     * @param config config bundle
     */
    @JvmOverloads
    constructor(
        file: File,
        loadPolicy: Int = DEFAULT_LOAD_POLICY,
        config: Config? = null,
    ) : this(createBackingDictionary(file), loadPolicy, config)

    /**
     * Loads data from the specified URL using the specified load policy.
     *
     * Constructs a new RAMDictionary that will load the contents the specified Wordnet data using the default load policy.
     *
     * @param url an url pointing to a local copy of Wordnet
     * @param loadPolicy the load policy of the dictionary
     * @param config config bundle
     */
    @JvmOverloads
    constructor(
        url: URL,
        loadPolicy: Int = DEFAULT_LOAD_POLICY,
        config: Config? = null,
    ) : this(createBackingDictionary(url), loadPolicy, config)

    init {
        configure(config)
    }

    override fun configure(config: Config?) {
        backingDictionary.configure(config)
    }

    // L O A D

    override fun startLoad(): Boolean {

        // behavior when loading from a backing dictionary depends on the load policy
        val result = backingDictionary.open()
        if (result) {
            try {
                when (loadPolicy) {
                    IMMEDIATE_LOAD  -> load(true)
                    BACKGROUND_LOAD -> load(false)
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
                return false
            }
        }
        return result
    }

    /**
     * This thread loads the dictionary data into memory and sets the appropriate variable in the parent dictionary.
     */
    override fun makeThread(): Thread {
        val t = Thread {
            try {
                val loader = DataLoader(backingDictionary)
                data = loader.call()
                backingDictionary.close()
            } catch (t: Throwable) {
                if (!Thread.currentThread().isInterrupted) {
                    t.printStackTrace()
                    System.err.println("Unable to load dictionary data into memory")
                }
            }
        }
        t.name = "Dictionary Loader"
        return t
    }

    /**
     * This is an internal utility method that determines whether this
     * dictionary should be considered open or closed.
     *
     * @return the lifecycle state object representing open if the object is open; otherwise the lifecycle state object representing closed
     */
    private fun assertLifecycleState(): LifecycleState {
        try {
            lifecycleLock.lock()

            // if the data object is present, then we are open
            if (data != null) {
                return LifecycleState.OPEN
            }

            // if the backing dictionary is present and open, then we are open
            if (backingDictionary.isOpen) {
                return LifecycleState.OPEN
            }

            // otherwise we are closed
            return LifecycleState.CLOSED
        } finally {
            lifecycleLock.unlock()
        }
    }

    override fun close() {
        try {
            lifecycleLock.lock()

            // if we are already closed, do nothing
            if (state == LifecycleState.CLOSED) {
                return
            }

            // if we are already closing, do nothing
            if (state != LifecycleState.CLOSING) {
                return
            }

            state = LifecycleState.CLOSING

            // stop loading first
            if (loader != null) {
                loader!!.interrupt()
                try {
                    loader!!.join()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                loader = null
            }

            // next close backing dictionary if it exists
            backingDictionary.close()

            // null out backing data
            data = null
        } finally {
            state = assertLifecycleState()
            lifecycleLock.unlock()
        }
    }

    // L O O K   U P

    override fun getIndex(id: IndexID): Index? {
        return if (data != null) super.getIndex(id) else return backingDictionary.getIndex(id)
    }

    override fun getSense(id: SenseID): Synset.Sense? {
        return if (data != null) super.getSense(id) else backingDictionary.getSense(id)
    }

    override fun getSense(sensekey: SenseKey): Synset.Sense? {
        return if (data != null) super.getSense(sensekey) else backingDictionary.getSense(sensekey)
    }

    override fun getLemmasStartingWith(start: String, pos: POS?, limit: Int): Set<String> {
        return if (data != null) super.getLemmasStartingWith(start, pos, limit) else backingDictionary.getLemmasStartingWith(start, pos, limit)
    }

    override fun getSynset(id: SynsetID): Synset? {
        return if (data != null) super.getSynset(id) else return backingDictionary.getSynset(id)
    }

    override fun getSenseEntry(key: SenseKey): SenseEntry? {
        return if (data != null) super.getSenseEntry(key) else backingDictionary.getSenseEntry(key)
    }

    override fun getExceptionEntry(id: ExceptionKey): ExceptionEntry? {
        return if (data != null) super.getExceptionEntry(id) else backingDictionary.getExceptionEntry(id)
    }

    // I T E R A T E

    override fun getIndexIterator(pos: POS): Iterator<Index> {
        return HotSwappableIndexIterator(pos)
    }

    override fun getSynsetIterator(pos: POS): Iterator<Synset> {
        return HotSwappableSynsetIterator(pos)
    }

    override fun getSenseEntryIterator(): Iterator<SenseEntry> {
        return HotSwappableSenseEntryIterator()
    }

    override fun getExceptionEntryIterator(pos: POS): Iterator<ExceptionEntry> {
        return HotSwappableExceptionEntryIterator(pos)
    }

    /**
     * An iterator that allows the dictionary to be loaded into memory while it is iterating.
     *
     * Constructs a new hot swappable iterator.
     *
     * @param itr the wrapped iterator
     * @param <E> the element type of the iterator
     * @param checkForLoad if true, on each call the iterator checks to see if the dictionary has been loaded into memory, switching data sources if so
     */
    private abstract inner class HotSwappableIterator<E>(
        private var itr: Iterator<E>,
        private var checkForLoad: Boolean,
    ) : Iterator<E> {

        private var last: E? = null

        override fun hasNext(): Boolean {
            if (checkForLoad) {
                checkForLoad()
            }
            return itr.hasNext()
        }

        override fun next(): E {
            if (checkForLoad) {
                checkForLoad()
                last = itr.next()
                return last!!
            } else {
                return itr.next()
            }
        }

        /**
         * Checks to see if the data has been loaded into memory
         * If so, replaces the original iterator with one that iterates over the in-memory data structures.
         */
        fun checkForLoad() {
            if (data == null) {
                return
            }
            checkForLoad = false
            itr = makeIterator()
            if (last != null) {
                while (itr.hasNext()) {
                    val consume: E? = itr.next()
                    if (last == consume) {
                        return
                    }
                }
                throw IllegalStateException()
            }
        }

        /**
         * Constructs the iterator that will iterate over the loaded data.
         *
         * @return the new iterator to be swapped in when loading is done
         */
        abstract fun makeIterator(): Iterator<E>
    }

    /**
     * A hot swappable iterator for indexes.
     *
     * @param pos the part-of-speech for the iterator
     */
    private inner class HotSwappableIndexIterator(private val pos: POS) :
        HotSwappableIterator<Index>(
            if (data == null) backingDictionary.getIndexIterator(pos) else data!!.indexes[pos]!!.values.iterator(),
            data == null
        ) {

        override fun makeIterator(): Iterator<Index> {
            val m = data!!.indexes[pos]!!
            return m.values.iterator()
        }
    }

    /**
     * A hot swappable iterator for synsets.
     *
     * @param pos the part-of-speech for the iterator
     */
    private inner class HotSwappableSynsetIterator(private val pos: POS) :
        HotSwappableIterator<Synset>(
            if (data == null) backingDictionary.getSynsetIterator(pos) else data!!.synsets[pos]!!.values.iterator(),
            data == null
        ) {

        override fun makeIterator(): Iterator<Synset> {
            val m = data!!.synsets[pos]!!
            return m.values.iterator()
        }
    }

    /**
     * A hot swappable iterator that iterates over exceptions entries for a particular part-of-speech.
     *
     * @param pos the part-of-speech for this iterator
     */
    private inner class HotSwappableExceptionEntryIterator(private val pos: POS) :
        HotSwappableIterator<ExceptionEntry>(
            if (data == null) backingDictionary.getExceptionEntryIterator(pos) else data!!.exceptions[pos]!!.values.iterator(),
            data == null
        ) {

        override fun makeIterator(): Iterator<ExceptionEntry> {
            return data!!.exceptions[pos]!!.values.iterator()
        }
    }

    /**
     * A hot swappable iterator that iterates over sense entries.
     */
    private inner class HotSwappableSenseEntryIterator :
        HotSwappableIterator<SenseEntry>(
            if (data == null) backingDictionary.getSenseEntryIterator() else data!!.senseEntries.values.iterator(),
            data == null
        ) {

        override fun makeIterator(): Iterator<SenseEntry> {
            return data!!.senseEntries.values.iterator()
        }
    }

    /**
     * A Callable that creates a dictionary data from a specified dictionary.
     * The data loader does not change the open state of the dictionary.
     * The dictionary for the loader must be open for the loader to function without throwing an exception.
     * The loader may be called multiple times (in a thread-safe manner) as long as the dictionary is open.
     *
     * Constructs a new data loader object, that uses the specified dictionary to load its data.
     *
     * @param source source dictionary
     */
    class DataLoader(private val source: IDictionary) : Callable<DictionaryData> {

        fun cooperate(t: Thread) {
            if (t.isInterrupted)
                throw InterruptedException()
        }

        @Throws(InterruptedException::class)
        override fun call(): DictionaryData {
            val thread = Thread.currentThread()

            // indexes
            val indexes = POS.entries
                .associate { pos ->
                    pos to source.getIndexIterator(pos)
                        .asSequence()
                        .associate { idx -> idx.iD to idx }
                }
            cooperate(thread)

            // synsets
            val synsets = POS.entries
                .associate { pos ->
                    pos to source.getSynsetIterator(pos)
                        .asSequence()
                        .associate { synset -> synset.iD to synset }
                }
            cooperate(thread)

            // exceptions
            val exceptions = POS.entries
                .associate { pos ->
                    pos to source.getExceptionEntryIterator(pos)
                        .asSequence()
                        .associate { exception -> exception.iD to exception }
                }
            cooperate(thread)

            // sense entries
            val senseEntries = source.getSenseEntryIterator().asSequence()
                .associate { entry ->
                    entry.senseKey.sensekey to SenseEntry(entry.senseKey, entry.offset, entry.senseNumber, entry.tagCount)
                }
            cooperate(thread)

            // senses
            val senses = synsets.asSequence()
                .flatMap { (_, m) -> m.values }
                .flatMap { synset -> synset.senses.asSequence() }
                .associate { sense ->
                    sense.senseKey.sensekey to sense
                }
            cooperate(thread)

            return DictionaryData(source.version, indexes, synsets, exceptions, senses, senseEntries)
        }
    }

    companion object {

        /**
         * The default load policy of a RAMDictionary is to load data in the background when opened.
         */
        const val DEFAULT_LOAD_POLICY: Int = BACKGROUND_LOAD

        /**
         * Creates a DataSourceDictionary out of the specified file, as long as the file points to an existing local directory.
         *
         * @param file the local directory for which to create a data source dictionary
         * @return a dictionary object that uses the specified local directory as its data source
         */
        fun createBackingDictionary(file: File): IDictionary {
            if (!FileProvider.Companion.isLocalDirectory(file))
                throw RuntimeException("Not a local directory")
            return DataSourceDictionary(FileProvider(file))
        }

        /**
         * Creates a DataSourceDictionary out of the specified url, as long as the url points to an existing local directory.
         *
         * @param url the local directory for which to create a data source dictionary
         * @return a dictionary object that uses the specified local directory as its data source
         */
        fun createBackingDictionary(url: URL): IDictionary {
            if (!FileProvider.Companion.isLocalDirectory(url))
                throw RuntimeException("Not a local directory")
            return DataSourceDictionary(FileProvider(url))
        }
    }
}