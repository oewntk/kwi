/*
 * Copyright (c) 2025.
 * Java Wordnet Interface Library (JWI) v2.4.0 Copyright (c) 2007-2015 Mark A. Finlayson
 * Kotlin Wordnet Interface Library (KWI) v1.0 Copyright (c) 2025 Bernard Bou
 * JWI is distributed under the terms of the Creative Commons Attribution 4.0 International Public License
 * KWI is distributed under the terms of the GPL3 License
 */

package org.kwi

import org.kwi.data.LoadPolicy.IMMEDIATE_LOAD
import java.io.File
import java.net.URL

object DictionaryFactory {

    val defaultFactory: (url: URL, config: Config?) -> IDictionary = { url: URL, config: Config? -> Dictionary(url, config) }
    val nonCachingFactory: (url: URL, config: Config?) -> IDictionary = { url: URL, config: Config? -> DataSourceDictionary(url, config) }
    val ramFactory: (url: URL, config: Config?) -> IDictionary = { url: URL, config: Config? -> RAMDictionary(url, IMMEDIATE_LOAD, config) }

    /**
     * Make dictionary from files in directory
     *
     * @param wnHome WordNet dictionary directory
     * @param config config
     * @param factory dictionary factory, which determines the type of dictionary to create (source, cached, ram)
     */
    @JvmStatic
    fun fromFile(
        wnHome: String,
        config: Config? = null,
        factory: (url: URL, config: Config?) -> IDictionary = defaultFactory,
    ): IDictionary {
        println("FROM DIR $wnHome")
        val url = File(wnHome).toURI().toURL()
        return fromURL(url, config, factory)
    }

    /**
     * Make dictionary from files in directory
     *
     * @param url WordNet dictionary URL
     * @param config config
     * @param factory dictionary factory, which determines the type of dictionary to create (source, cached, ram)
     */
    @JvmStatic
    fun fromURL(
        url: URL,
        config: Config? = null,
        factory: (url: URL, config: Config?) -> IDictionary = defaultFactory,
    ): IDictionary {
        println("FROM URL $url")

        // construct the dictionary object
        val dict = factory.invoke(url, config)

        // open it
        dict.open()
        return dict
    }

    @JvmStatic
    fun fromSer(
        /** The serialized dictionary file */
        serPath: String,
    ): IDictionary {
        println("FROM SER $serPath")

        // deserialize from file
        val dict = DeserializedRAMDictionary(serPath)

        // open it
        dict.open()
        return dict
    }

    @JvmStatic
    fun factory(tag: String?): (url: URL, config: Config?) -> IDictionary {
        println("Factory: $tag")
        return when (tag) {
            "SOURCE" -> nonCachingFactory
            "RAM"    -> ramFactory
            else     -> defaultFactory
        }
    }
}