/*
 * Copyright (c) 2025.
 * Java Wordnet Interface Library (JWI) v2.4.0 Copyright (c) 2007-2015 Mark A. Finlayson
 * Kotlin Wordnet Interface Library (KWI) v1.0 Copyright (c) 2025 Bernard Bou
 * JWI is distributed under the terms of the Creative Commons Attribution 4.0 International Public License
 * KWI is distributed under the terms of the GPL3 License
 */

package org.kwi

import org.kwi.data.IHasLifecycle.LifecycleState
import org.kwi.data.ILoadable
import org.kwi.item.*
import java.io.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import java.util.zip.GZIPOutputStream

/**
 * Dictionary that can be completely loaded into memory.
 * **Note:** If you receive an OutOfMemoryError while using this object, try increasing your heap size, by using the `-Xmx` switch.
 */
abstract class BaseRAMDictionary protected constructor(
) : IDictionary, ILoadable {

    internal val lifecycleLock: Lock = ReentrantLock()

    private val loadLock: Lock = ReentrantLock()

    @Volatile
    internal var state: LifecycleState = LifecycleState.CLOSED

    @Transient
    internal var loader: Thread? = null

    /**
     * Dictionary data
     */
    internal var data: DictionaryData? = null

    // L O A D

    override val isLoaded: Boolean
        get() = data != null

    override fun load() {
        try {
            load(false)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    @Throws(InterruptedException::class)
    override fun load(block: Boolean) {
        if (loader != null) {
            return
        }
        try {
            loadLock.lock()

            // if we are closed or in the process of closing, do nothing
            if (state == LifecycleState.CLOSED || state == LifecycleState.CLOSING) {
                return
            }

            if (loader != null) {
                return
            }
            println("loading...")
            loader = makeThread()
            loader!!.isDaemon = true
            loader!!.start()
            if (block) {
                loader!!.join()
            }
            println("loaded")
        } finally {
            loadLock.unlock()
        }
    }

    abstract fun startLoad(): Boolean

    abstract fun makeThread(): Thread

    // OPEN

    override val isOpen: Boolean
        get() {
            try {
                lifecycleLock.lock()
                return state == LifecycleState.OPEN
            } finally {
                lifecycleLock.unlock()
            }
        }

    @Throws(IOException::class)
    override fun open(): Boolean {
        try {
            lifecycleLock.lock()

            // if the dictionary is already open, return true
            if (state == LifecycleState.OPEN) {
                return true
            }

            // if the dictionary is not closed, return false
            if (state != LifecycleState.CLOSED) {
                return false
            }

            // indicate the start of opening
            state = LifecycleState.OPENING

            return startLoad()

        } finally {
            // make sure to clear the opening state
            state = assertLifecycleState()
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

            // null out backing data
            data = null
        } finally {
            state = assertLifecycleState()
            lifecycleLock.unlock()
        }
    }

    /**
     * This is an internal utility method that determines whether this dictionary should be considered open or closed.
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

            // otherwise we are closed
            return LifecycleState.CLOSED
        } finally {
            lifecycleLock.unlock()
        }
    }

    // V E R S I O N

    override val version: Version?
        get() {
            check(data != null) { NO_DATA }
            return data!!.version
        }

    // L O O K   U P

    // INDEX

    override fun getIndex(lemma: String, pos: POS): Index? {
        return getIndex(IndexID(lemma, pos))
    }

    override fun getIndex(id: IndexID): Index? {
        check(data != null) { NO_DATA }
        return data!!.indexes[id.pOS]!![id]
    }

    // SENSE

    override fun getSense(id: SenseID): Synset.Sense? {
        check(data != null) { NO_DATA }
        val resolver = data!!.synsets[id.pOS]!!
        val synset = resolver[id.synsetID]
        if (synset == null) {
            return null
        }
        return when (id) {
            is SenseIDWithNum   -> synset.senses[id.senseNumber - 1]
            is SenseIDWithLemma -> synset.senses.first { it.lemma.equals(id.lemma, ignoreCase = true) }
            else                -> throw IllegalArgumentException("Not enough information in SenseID instance to retrieve sense.")
        }
    }

    override fun getSense(sk: SenseKey): Synset.Sense? {
        check(data != null) { NO_DATA }
        return data!!.senses[sk.sensekey]
    }

    override fun getLemmasStartingWith(start: String, pos: POS?, limit: Int): Set<String> {
        check(data != null) { NO_DATA }
        return data!!.senses.values
            .filter { it.lemma.startsWith(start) && if (pos != null) it.pOS == pos else true }
            .map { it.lemma }
            .take(limit)
            .toSet()
    }

    // SYNSET

    override fun getSynset(id: SynsetID): Synset? {
        check(data != null) { NO_DATA }
        return data!!.synsets[id.pOS]!![id]
    }

    // SENSE ENTRY

    override fun getSenseEntry(key: SenseKey): SenseEntry? {
        check(data != null) { NO_DATA }
        return data!!.senseEntries[key.sensekey]
    }

    // EXCEPTION ENTRY

    override fun getExceptionEntry(surfaceForm: String, pos: POS): ExceptionEntry? {
        return getExceptionEntry(ExceptionKey(surfaceForm, pos))
    }

    override fun getExceptionEntry(id: ExceptionKey): ExceptionEntry? {
        check(data != null) { NO_DATA }
        return data!!.exceptions[id.pOS]!![id]
    }

    // I T E R A T E

    override fun getIndexIterator(pos: POS): Iterator<Index> {
        check(data != null) { NO_DATA }
        return data!!.indexes[pos]!!.values.iterator()
    }

    override fun getSynsetIterator(pos: POS): Iterator<Synset> {
        check(data != null) { NO_DATA }
        return data!!.synsets[pos]!!.values.iterator()
    }

    override fun getSenseEntryIterator(): Iterator<SenseEntry> {
        check(data != null) { NO_DATA }
        return data!!.senseEntries.values.iterator()
    }

    override fun getExceptionEntryIterator(pos: POS): Iterator<ExceptionEntry> {
        check(data != null) { NO_DATA }
        return data!!.exceptions[pos]!!.values.iterator()
    }

    // E X P O R T

    /**
     * Exports the in-memory contents of the data to the specified output stream.
     * This method flushes and closes the output stream when it is done writing
     * the data.
     *
     * @param out the output stream to which the in-memory data will be written
     * @throws IOException           if there is a problem writing the in-memory data to the output stream.
     * @throws IllegalStateException if the dictionary has not been loaded into memory
     */
    @Throws(IOException::class)
    fun export(out: OutputStream) {
        try {
            loadLock.lock()
            check(isLoaded) { "RAMDictionary not loaded into memory" }

            GZIPOutputStream(out).use {
                BufferedOutputStream(it).use {
                    ObjectOutputStream(it).use {
                        it.writeObject(data)
                        it.flush()
                    }
                }
            }
        } finally {
            loadLock.unlock()
        }
    }

    // D I C T I O N A R Y   L I V E   D A T A

    /**
     * Object that holds all the dictionary data loaded from the Wordnet files.
     *
     * Constructs an empty dictionary data object.
     */
    data class DictionaryData(
        val version: Version?,
        val indexes: Map<POS, Map<IndexID, Index>>,
        val synsets: Map<POS, Map<SynsetID, Synset>>,
        val exceptions: Map<POS, Map<ExceptionKey, ExceptionEntry>>,
        val senses: Map<String, Synset.Sense>,
        val senseEntries: Map<String, SenseEntry>,
    ) : Serializable

    companion object {

        const val NO_DATA = "Data not loaded into memory"
    }
}