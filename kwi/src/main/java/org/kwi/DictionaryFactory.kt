package org.kwi

import org.kwi.data.FileProvider
import org.kwi.data.LoadPolicy.IMMEDIATE_LOAD
import java.io.File
import java.net.URL

object DictionaryFactory {

    val defaultFactory: (url: URL, config: Config?) -> IDictionary = { url: URL, config: Config? -> Dictionary(url, config) }
    val nonCachingFactory: (url: URL, config: Config?) -> IDictionary = { url: URL, config: Config? -> DataSourceDictionary(FileProvider(url), config) }
    val ramFactory: (url: URL, config: Config?) -> IDictionary = { url: URL, config: Config? -> RAMDictionary(url, IMMEDIATE_LOAD, config) }

    @JvmStatic
    fun fromFile(
        /**
         * the WordNet dictionary directory
         */
        wnHome: String,
        config: Config? = null,
        factory: (url: URL, config: Config?) -> IDictionary = defaultFactory,
    ): IDictionary {
        println("FROM DIR $wnHome")
        val url = File(wnHome).toURI().toURL()
        return fromURL(url, config, factory)
    }

    @JvmStatic
    fun fromURL(
        /**
         * the URL to the WordNet dictionary directory
         */
        url: URL,
        config: Config? = null,
        factory: (url: URL, config: Config?) -> IDictionary = defaultFactory,
    ): IDictionary {
        println("FROM URL $url")

        // construct the dictionary object and open it
        val dict = factory.invoke(url, config)

        // open it
        dict.open()
        return dict
    }

    @JvmStatic
    fun fromSer(
        /**
         * The serialized dictionary file
         */
        serPath: String,
    ): IDictionary {
        println("FROM SER $serPath")
        return DeserializedRAMDictionary(serPath)
    }

    @JvmStatic
    fun makeFactory(tag: String?): (url: URL, config: Config?) -> IDictionary {
        println("URL dictionary factory: $tag")
        return when (tag) {
            "SOURCE" -> { url: URL, config: Config? -> DataSourceDictionary(url, config) }
            "RAM"    -> { url: URL, config: Config? -> RAMDictionary(url, IMMEDIATE_LOAD, config) }
            else     -> { url: URL, config: Config? -> Dictionary(url, config) }
        }
    }
}