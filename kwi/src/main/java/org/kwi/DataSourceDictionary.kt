package org.kwi

import org.kwi.data.ContentType
import org.kwi.data.DataType
import org.kwi.data.FileProvider
import org.kwi.data.IHasCharset
import org.kwi.data.IHasLifecycle.ObjectClosedException
import org.kwi.data.parse.ILineParser
import org.kwi.item.ExceptionEntry
import org.kwi.item.ExceptionEntryProxy
import org.kwi.item.ExceptionKey
import org.kwi.item.IHasPOS
import org.kwi.item.Index
import org.kwi.item.IndexID
import org.kwi.item.POS
import org.kwi.item.Pointer
import org.kwi.item.SenseEntry
import org.kwi.item.SenseID
import org.kwi.item.SenseIDWithLemma
import org.kwi.item.SenseIDWithNum
import org.kwi.item.SenseKey
import org.kwi.item.Synset
import org.kwi.item.SynsetID
import org.kwi.item.Version
import java.io.IOException
import java.net.URL
import java.nio.charset.Charset
import java.util.Collections

/**
 * A type of `IDictionary` which uses an instance of a `DataProvider` to obtain its data.

 * Basic implementation of the `IDictionary` interface. A path to the
 * Wordnet dictionary files must be provided. If no `IDataProvider` is
 * specified, it uses the default implementation provided with the distribution.
 *
 * Constructs a dictionary with a caller-specified `IDataProvider`.
 *
 * @param dataProvider data provider
 * @param config config bundle
 */
class DataSourceDictionary(
    val dataProvider: FileProvider,
    config: Config? = null,
) : IDictionary, IHasCharset {

    /**
     * Constructs a new dictionary that uses the Wordnet files located in a directory pointed to by the specified url
     *
     * @param wordnetDir an url pointing to a directory containing the Wordnet data files on the filesystem
     * @param config config parameters
     */
    @JvmOverloads
    constructor(wordnetDir: URL, config: Config? = null) : this(FileProvider(wordnetDir))

    init {
        configure(config)
    }

    override val version: Version?
        get() {
            checkOpen()
            return dataProvider.version
        }

    // C O N F I G

    /**
     * Sets the character set associated with this dictionary.
     *
     * @param charset the possibly null character set to use when decoding files.
     */
    override var charset: Charset?
        get() = dataProvider.charset
        set(charset) {
            dataProvider.charset = charset
        }

    /**
     * Configure from config bundle
     */
    override fun configure(config: Config?) {
        // default
        charset = Charset.defaultCharset()

        // enforce config
        if (config == null) {
            return
        }

        // global params
        if (config.checkLexicalId != null) {
            Synset.checkLexicalId = config.checkLexicalId == true
        }

        // dictionary params
        if (config.charSet != null) {
            charset = config.charSet
        }
    }

    // O P E N  /  C L O S E

    @Throws(IOException::class)
    override fun open(): Boolean {
        return dataProvider.open()
    }

    override fun close() {
        dataProvider.close()
    }

    override val isOpen: Boolean
        get() = dataProvider.isOpen

    /**
     * An internal method for assuring compliance with the dictionary interface
     * that says that methods will throw `ObjectClosedException`s if
     * the dictionary has not yet been opened.
     *
     * @throws ObjectClosedException if the dictionary is closed.
     */
    private fun checkOpen() {
        if (!isOpen) {
            throw ObjectClosedException()
        }
    }

    // L O O K  U P

    override fun getIndex(lemma: String, pos: POS): Index? {
        checkOpen()
        return getIndex(IndexID(lemma, pos))
    }

    override fun getIndex(id: IndexID): Index? {
        checkOpen()
        val content = dataProvider.resolveContentType(DataType.INDEX, id.pOS)!!
        val file = dataProvider.getSource(content)!!
        val line = file.getLine(id.lemma) ?: return null
        return content.dataType.parser.parseLine(line)
    }

    override fun getSense(id: SenseID): Synset.Sense? {
        checkOpen()
        val synset = getSynset(id.synsetID)
        if (synset == null) {
            return null
        }
        return when (id) {
            is SenseIDWithNum   -> synset.senses[id.senseNumber - 1]
            is SenseIDWithLemma -> synset.senses.first { it.lemma.equals(id.lemma, ignoreCase = true) }
            else                -> throw IllegalArgumentException("Not enough information in SenseID instance to retrieve sense.")
        }
    }

    override fun getSense(sensekey: SenseKey): Synset.Sense? {
        checkOpen()

        // no need to cache result from the following calls as this will have been done in the call to getSynset()
        val entry = getSenseEntry(sensekey)
        if (entry != null) {
            val synset = getSynset(SynsetID(entry.offset, entry.pOS))
            return synset?.senses?.first { it.senseKey == sensekey }
        }

        // sometimes the sense.index file doesn't have the sense key entry so try an alternate method of retrieving senses by sense keys
        // we have to search the synonyms of the sense returned from the index search because some synsets have lemmas that differ only in case e.g., {earth, Earth} or {south, South}, and so separate entries are not found in the index file
        return getIndex(sensekey.lemma, sensekey.pOS)?.senseIDs
            ?.mapNotNull { getSense(it) }
            ?.flatMap { it.synset.senses.asSequence() }
            ?.first { it.senseKey == sensekey }
    }

    override fun getSenseEntry(key: SenseKey): SenseEntry? {
        checkOpen()
        val content = dataProvider.resolveContentType(DataType.SENSE, null)!!
        val file = dataProvider.getSource(content)!!
        val line = file.getLine(key.toString()) ?: return null
        return content.dataType.parser.parseLine(line)
    }

    override fun getSynset(id: SynsetID): Synset? {
        checkOpen()
        val content = dataProvider.resolveContentType(DataType.DATA, id.pOS)!!
        val file = dataProvider.getSource(content)!!
        val zeroFilledOffset = Synset.zeroFillOffset(id.offset)
        val line = file.getLine(zeroFilledOffset) ?: return null
        val synset = content.dataType.parser.parseLine(line)
        if (synset.isAdjectiveSatellite) {
            synset.adjHead = getAdjHead(synset)
        }
        return synset
    }

    override fun getExceptionEntry(surfaceForm: String, pos: POS): ExceptionEntry? {
        return getExceptionEntry(ExceptionKey(surfaceForm, pos))
    }

    override fun getExceptionEntry(id: ExceptionKey): ExceptionEntry? {
        checkOpen()
        val content = dataProvider.resolveContentType(DataType.EXCEPTION, id.pOS)!!
        val file = dataProvider.getSource(content)!!
        val line = file.getLine(id.surfaceForm) ?: return null
        val proxy = content.dataType.parser.parseLine(line)
        return ExceptionEntry(proxy, id.pOS)
    }

    override fun getLemmasStartingWith(start: String, pos: POS?, limit: Int): Set<String> {
        checkOpen()
        val seq: Sequence<String> = if (pos != null) getSequenceStartingWith(start, pos) else POS.entries.asSequence().flatMap { getLemmasStartingWith(start, it) }
        return seq
            .run { if (limit > 0) take(limit) else this }
            .toSortedSet()
    }

    private fun getSequenceStartingWith(start: String, pos: POS): Sequence<String> {
        checkOpen()
        val content = dataProvider.resolveContentType(DataType.INDEX, pos)!!
        val parser = content.dataType.parser
        val file = dataProvider.getSource<Index>(content)!!
        val lines = file.iterator(start)
        return lines.asSequence()
            .filter { it.startsWith(start) }
            .map { parser.parseLine(it).lemma }
    }

    // I T E R A T E

    override fun getIndexIterator(pos: POS): Iterator<Index> {
        checkOpen()
        return IndexFileIterator(pos)
    }

    override fun getSynsetIterator(pos: POS): Iterator<Synset> {
        checkOpen()
        return DataFileIterator(pos)
    }

    override fun getExceptionEntryIterator(pos: POS): Iterator<ExceptionEntry> {
        checkOpen()
        return ExceptionFileIterator(pos)
    }

    override fun getSenseEntryIterator(): Iterator<SenseEntry> {
        checkOpen()
        return SenseEntryFileIterator()
    }

    // F I L E   I T E R A T O R S

    /**
     * Abstract class used for iterating over line-based files.
     */
    abstract inner class FileIterator<T, N> @JvmOverloads constructor(content: ContentType<T>, startKey: String? = null) : Iterator<N>, IHasPOS {

        protected val source = dataProvider.getSource<T>(content)

        protected var iterator: Iterator<String>? = source?.iterator(startKey) ?: Collections.emptyIterator<String>() // Fix for Bug018

        protected val parser: ILineParser<T> = content.dataType.parser

        var currentLine: String? = null

        override val pOS: POS
            get() {
                val contentType = source!!.contentType
                return contentType.pOS!!
            }

        override fun hasNext(): Boolean {
            return iterator!!.hasNext()
        }

        override fun next(): N {
            currentLine = iterator!!.next()
            return parseLine(currentLine!!)!!
        }

        /**
         * Parses the line using a parser provided at construction time
         *
         * @param line line
         * @return parsed object
         */
        abstract fun parseLine(line: String): N
    }

    /**
     * A file iterator where the data type returned by the iterator is the same
     * as that returned by the backing data source.
     */
    abstract inner class FileIterator2<T> : FileIterator<T, T> {

        /**
         * Constructs a new file iterator with the specified content type.
         *
         * @param content content type
         */
        constructor(content: ContentType<T>) : super(content)

        /**
         * Constructs a new file iterator with the specified content type and start key.
         *
         * @param content content type
         * @param startKey start key
         */
        constructor(content: ContentType<T>, startKey: String) : super(content, startKey)
    }

    /**
     * Iterates over index files.
     */
    inner class IndexFileIterator @JvmOverloads constructor(pos: POS, pattern: String = "") : FileIterator2<Index>(
        dataProvider.resolveContentType<Index>(DataType.INDEX, pos)!!,
        pattern
    ) {

        override fun parseLine(line: String): Index {
            return parser.parseLine(line)
        }
    }

    /**
     * Iterates over the sense file.
     */
    inner class SenseEntryFileIterator : FileIterator2<SenseEntry>(
        dataProvider.resolveContentType<SenseEntry>(DataType.SENSE, null)!!
    ) {

        override fun parseLine(line: String): SenseEntry {
            return parser.parseLine(line)
        }
    }

    /**
     * Iterates over data files.
     */
    inner class DataFileIterator(pos: POS?) : FileIterator2<Synset>(
        dataProvider.resolveContentType<Synset>(DataType.DATA, pos)!!
    ) {

        override fun parseLine(line: String): Synset {
            if (pOS == POS.ADJECTIVE) {
                val synset = parser.parseLine(line)
                if (synset.isAdjectiveSatellite) {
                    synset.adjHead = getAdjHead(synset)
                }
                return synset
            } else {
                return parser.parseLine(line)
            }
        }
    }

    /**
     * Iterates over exception files.
     */
    inner class ExceptionFileIterator(pos: POS?) : FileIterator<ExceptionEntryProxy, ExceptionEntry>(
        dataProvider.resolveContentType<ExceptionEntryProxy>(DataType.EXCEPTION, pos)!!
    ) {

        override fun parseLine(line: String): ExceptionEntry {
            val proxy = parser.parseLine(line)
            return ExceptionEntry(proxy, pOS)
        }
    }

    // H E A D

    /**
     * This method gets the head word and head ID on the specified synset by searching in the dictionary to find the head of its cluster.
     * We will assume the head is the first adjective head synset related by an '&amp;' pointer (SIMILAR_TO) to this synset.
     *
     * @param synset synset
     */
    private fun getAdjHead(synset: Synset): Synset.Head {
        // head words are only needed for adjective satellites
        require(synset.isAdjectiveSatellite)

        // go find the head synset
        // assume first 'similar' adjective head is the right one
        val headSynset: Synset = synset.getRelatedSynsetsFor(Pointer.SIMILAR_TO)
            .asSequence()
            .map { getSynset(it)!! }
            .filter { it.isAdjectiveHead }
            .first()
        val headSense: Synset.Sense = headSynset.senses[0]

        // version 1.6 of Wordnet adds the adjective marker symbol on the end of the head word lemma
        var headLemma = headSense.lemma
        val isVer16 = version != null && version!!.majorVersion == 1 && version!!.minorVersion == 6
        if (isVer16 && headSense.adjectiveMarker != null) {
            headLemma += headSense.adjectiveMarker!!.symbol
        }
        return Synset.Head(headLemma, headSense.lexicalID)
    }
}