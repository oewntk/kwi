package org.kwi

import org.kwi.data.IHasLifecycle
import org.kwi.data.IHasLifecycle.ObjectClosedException
import org.kwi.item.*
import java.io.IOException
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * A dictionary that caches the results of another dictionary
 */
open class CachingDictionary(
    /**
     * The dictionary that is wrapped by this dictionary
     */
    val backingDictionary: IDictionary,

    ) : IDictionary {

    /**
     * Cache
     */
    val cache = ItemCache()

    /**
     * Configure
     */
    override fun configure(config: Config?) {
        backingDictionary.configure(config)
    }

    // O P E N   /   C L O S E

    /**
     * An internal method for assuring compliance with the dictionary interface that says that methods will throw ObjectClosedExceptions if the dictionary has not yet been opened.
     *
     * @throws ObjectClosedException if the dictionary is closed.
     */
    protected fun checkOpen() {
        if (isOpen) {
            if (!cache.isOpen) {
                try {
                    cache.open()
                } catch (e: IOException) {
                    throw ObjectClosedException(e)
                }
            }
        } else {
            if (cache.isOpen) {
                cache.close()
            }
            throw ObjectClosedException()
        }
    }

    @Throws(IOException::class)
    override fun open(): Boolean {
        if (isOpen) {
            return true
        }
        cache.open()
        return backingDictionary.open()
    }

    override val isOpen: Boolean
        get() = backingDictionary.isOpen

    override fun close() {
        if (!isOpen) {
            return
        }
        cache.close()
        backingDictionary.close()
    }

    override val version: Version?
        get() {
            return backingDictionary.version
        }

    // L O O K   U P

    override fun getIndex(lemma: String, pos: POS): Index? {
        checkOpen()
        val id = IndexID(lemma, pos)
        var item = cache.retrieveItem(id)
        if (item == null) {
            item = backingDictionary.getIndex(id)
            if (item != null) {
                cache.cacheItem(item)
            }
        }
        return item as Index?
    }

    override fun getIndex(id: IndexID): Index? {
        checkOpen()
        var item = cache.retrieveItem(id)
        if (item == null) {
            item = backingDictionary.getIndex(id)
            if (item != null) {
                cache.cacheItem(item)
            }
        }
        return item as Index?
    }

    override fun getSense(id: SenseID): Synset.Sense? {
        checkOpen()
        var item = cache.retrieveItem(id)
        if (item == null) {
            item = backingDictionary.getSense(id)
            if (item != null) {
                cacheSynset(item.synset)
            }
        }
        return item as Synset.Sense?
    }

    override fun getSense(sensekey: SenseKey): Synset.Sense? {
        checkOpen()
        var item = cache.retrieveSense(sensekey)
        if (item == null) {
            item = backingDictionary.getSense(sensekey)
            if (item != null) {
                cacheSynset(item.synset)
            }
        }
        return item
    }

    override fun getSynset(id: SynsetID): Synset? {
        checkOpen()
        var item = cache.retrieveItem(id)
        if (item == null) {
            item = backingDictionary.getSynset(id)
            if (item != null) {
                cacheSynset(item)
            }
        }
        return item as Synset?
    }

    /**
     * Caches the specified synset and its senses.
     *
     * @param synset the synset to be cached
     */
    protected fun cacheSynset(synset: Synset) {
        cache.cacheItem(synset)
        for (sense in synset.senses) {
            cache.cacheItem(sense)
            cache.cacheSenseByKey(sense)
        }
    }

    override fun getExceptionEntry(surfaceForm: String, pos: POS): ExceptionEntry? {
        checkOpen()
        val id = ExceptionKey(surfaceForm, pos)
        var item = cache.retrieveItem(id)
        if (item == null) {
            item = backingDictionary.getExceptionEntry(id)
            if (item != null) {
                cache.cacheItem(item)
            }
        }
        return item as ExceptionEntry?
    }

    override fun getExceptionEntry(id: ExceptionKey): ExceptionEntry? {
        checkOpen()
        var item = cache.retrieveItem(id)
        if (item == null) {
            item = backingDictionary.getExceptionEntry(id)
            if (item != null) {
                cache.cacheItem(item)
            }
        }
        return item as ExceptionEntry?
    }

    override fun getSenseEntry(key: SenseKey): SenseEntry? {
        checkOpen()
        var entry = cache.retrieveSenseEntry(key)
        if (entry == null) {
            entry = backingDictionary.getSenseEntry(key)
            if (entry != null) {
                cache.cacheSenseEntry(entry)
            }
        }
        return entry
    }

    // I T E R A T E

    override fun getIndexIterator(pos: POS): Iterator<Index> {
        return backingDictionary.getIndexIterator(pos)
    }

    override fun getSynsetIterator(pos: POS): Iterator<Synset> {
        return backingDictionary.getSynsetIterator(pos)
    }

    override fun getSenseEntryIterator(): Iterator<SenseEntry> {
        return backingDictionary.getSenseEntryIterator()
    }

    override fun getExceptionEntryIterator(pos: POS): Iterator<ExceptionEntry> {
        return backingDictionary.getExceptionEntryIterator(pos)
    }

    /**
     * An LRU cache for objects in KWI.
     *
     * Caller can specify both the initial size, maximum size, and the
     * initial state of caching.
     *
     * Default constructor that initializes the dictionary with caching enabled.
     *
     * @param initialCapacity0  the initial capacity of the cache
     * @param maximumCapacity0  the maximum capacity of the cache
     * @param isEnabled0        whether the cache starts out enabled
     */
    class ItemCache @JvmOverloads constructor(
        initialCapacity0: Int = DEFAULT_INITIAL_CAPACITY,
        maximumCapacity0: Int = DEFAULT_MAXIMUM_CAPACITY,
        isEnabled0: Boolean = true,
    ) : IHasLifecycle {

        private val lifecycleLock: Lock = ReentrantLock()

        private val cacheLock = Any()

        // The caches themselves

        var itemCache: MutableMap<IItemID, IItem<*>>? = null

        var keyCache: MutableMap<SenseKey, Synset.Sense>? = null

        var senseCache: MutableMap<SenseKey, SenseEntry>? = null

        var sensesCache: MutableMap<SenseKey, Array<SenseEntry>>? = null

        var initialCapacity: Int = initialCapacity0
            set(capacity) {
                field = if (capacity < 1) DEFAULT_INITIAL_CAPACITY else capacity
            }

        /**
         * The maximum capacity of this cache.
         */
        var maximumCapacity: Int = maximumCapacity0
            set(capacity) {
                val oldCapacity = maximumCapacity
                maximumCapacity = capacity
                if (maximumCapacity < 1 || oldCapacity <= maximumCapacity) {
                    return
                }
                reduceCacheSize(itemCache!!)
                reduceCacheSize(keyCache!!)
                reduceCacheSize(senseCache!!)
                reduceCacheSize(sensesCache!!)
            }

        /**
         * Brings the map size into line with the specified maximum capacity of
         * this cache.
         *
         * @param cache the map to be trimmed
         */
        private fun reduceCacheSize(cache: MutableMap<*, *>) {
            if (!isOpen || maximumCapacity < 1 || cache.size < maximumCapacity) {
                return
            }
            synchronized(cacheLock) {
                val remove = cache.size - maximumCapacity
                val itr: MutableIterator<*> = cache.keys.iterator()
                repeat(remove + 1) {
                    if (itr.hasNext()) {
                        itr.next()
                        itr.remove()
                    }
                }
            }
        }

        /**
         * Whether this cache is enabled
         * If a cache is enabled, it will cache an item passed to its `cache` methods
         */
        var isEnabled: Boolean = isEnabled0

        override fun open(): Boolean {
            if (isOpen) {
                return true
            }
            try {
                lifecycleLock.lock()
                val capacity = if (initialCapacity < 1) DEFAULT_INITIAL_CAPACITY else initialCapacity
                itemCache = makeCache<IItemID, IItem<*>>(capacity)
                keyCache = makeCache<SenseKey, Synset.Sense>(capacity)
                senseCache = makeCache<SenseKey, SenseEntry>(capacity)
                sensesCache = makeCache<SenseKey, Array<SenseEntry>>(capacity)
            } finally {
                lifecycleLock.unlock()
            }
            return true
        }

        /**
         * Creates the map that backs this cache.
         *
         * @param <K>             the key type
         * @param <V>             the value type
         * @param initialCapacity the initial capacity
         * @return the new map
         */
        private fun <K, V> makeCache(initialCapacity: Int): MutableMap<K, V> {
            return LinkedHashMap<K, V>(initialCapacity, DEFAULT_LOAD_FACTOR, true)
        }

        override val isOpen: Boolean
            get() = itemCache != null && keyCache != null && senseCache != null && sensesCache != null

        /**
         * An internal method for assuring compliance with the dictionary
         * interface that says that methods will throw
         * `ObjectClosedException`s if the dictionary has not yet been
         * opened.
         *
         * @throws ObjectClosedException if the dictionary is closed.
         */
        private fun checkOpen() {
            if (!isOpen) {
                throw ObjectClosedException()
            }
        }

        override fun close() {
            if (!isOpen) {
                return
            }
            try {
                lifecycleLock.lock()
                itemCache = null
                keyCache = null
                senseCache = null
                sensesCache = null
            } finally {
                lifecycleLock.unlock()
            }
        }

        /**
         * Removes all entries from the cache.
         */
        fun clear() {
            itemCache?.clear()
            keyCache?.clear()
            senseCache?.clear()
            sensesCache?.clear()
        }

        /**
         * The number of items in the cache.
         *
         * @return the number of items in the cache.
         */
        fun size(): Int {
            checkOpen()
            return itemCache!!.size + keyCache!!.size + senseCache!!.size + sensesCache!!.size
        }

        /**
         * Caches the specified item, if this cache is enabled. Otherwise, does nothing.
         *
         * @param item the item to be cached
         */
        fun cacheItem(item: IItem<*>) {
            checkOpen()
            if (!isEnabled) {
                return
            }
            val id = item.iD
            itemCache!!.put(id, item)
            reduceCacheSize(itemCache!!)
        }

        /**
         * Caches the specified sense, indexed by its sense key.
         *
         * @param sense the sense to be cached
         */
        fun cacheSenseByKey(sense: Synset.Sense) {
            checkOpen()
            if (!isEnabled) {
                return
            }
            keyCache!!.put(sense.senseKey, sense)
            reduceCacheSize(keyCache!!)
        }

        /**
         * Caches the specified entry.
         *
         * @param entry the entry to be cached
         */
        fun cacheSenseEntry(entry: SenseEntry) {
            checkOpen()
            if (!isEnabled) {
                return
            }
            senseCache!!.put(entry.senseKey, entry)
            reduceCacheSize(senseCache!!)
        }

        /**
         * Retrieves the item identified by the specified id.
         *
         * @param <T> the type of the item
         * @param <D> the type of the item id
         * @param id the id for the requested item
         * @return the item for the specified id, or null if not present in the cache
         */
        fun <T : IItem<D>, D : IItemID> retrieveItem(id: D): T? {
            checkOpen()
            @Suppress("UNCHECKED_CAST")
            return itemCache!![id] as T?
        }

        /**
         * Retrieves the sense identified by the specified sense key.
         *
         * @param sensekey the sense key for the requested sense
         * @return the sense for the specified key, or null if not
         * present in the cache
         */
        fun retrieveSense(sensekey: SenseKey): Synset.Sense? {
            checkOpen()
            return keyCache!![sensekey]
        }

        /**
         * Retrieves the sense entry identified by the specified sense key.
         *
         * @param sensekey the sense key for the requested sense entry
         * @return the sense entry for the specified key, or null if not present in the cache
         */
        fun retrieveSenseEntry(sensekey: SenseKey): SenseEntry? {
            checkOpen()
            return senseCache!![sensekey]
        }

        companion object {

            const val DEFAULT_INITIAL_CAPACITY: Int = 16
            const val DEFAULT_MAXIMUM_CAPACITY: Int = 512
            const val DEFAULT_LOAD_FACTOR: Float = 0.75f
        }
    }

    override fun getLemmasStartingWith(start: String, pos: POS?, limit: Int): Set<String> {
        checkOpen()
        return backingDictionary.getLemmasStartingWith(start, pos, limit)
    }
}