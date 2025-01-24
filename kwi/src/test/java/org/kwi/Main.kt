package org.kwi

import org.kwi.DictionaryFactory.factory
import org.kwi.DictionaryFactory.fromFile
import org.kwi.data.FileProvider
import org.kwi.data.LoadPolicy.IMMEDIATE_LOAD
import org.kwi.utils.Walker
import java.io.IOException
import java.net.URL

/**
 * Tree exploration
 *
 * @author Bernard Bou
 */
class Main {

    companion object {

        val nonCachingFactory: (url: URL, config: Config?) -> IDictionary = { url: URL, config: Config? -> DataSourceDictionary(FileProvider(url), config) }
        val ramFactory: (url: URL, config: Config?) -> IDictionary = { url: URL, config: Config? -> RAMDictionary(url, IMMEDIATE_LOAD, config) }

        private lateinit var walker: Walker

        private lateinit var factory: (url: URL, config: Config?) -> IDictionary

        fun walkWords(vararg words: String) {
            words.forEach {
                walker.walk(it)
            }
        }

        fun handleOptions(args: Array<String>): Array<String> {
            args
                .asSequence()
                .filter { it.startsWith("--") }
                .forEach {
                    when (it) {
                        "--ram"      -> factory = ramFactory
                        "--no-cache" -> factory = nonCachingFactory
                    }
                }
            return args.filter { !it.startsWith("--") }.toTypedArray()
        }

        @JvmStatic
        @Throws(IOException::class)
        fun main(args0: Array<String>) {
            val args = handleOptions(args0)
            val source = args[0]
            val words = args.sliceArray(1..args.size - 1)
            val ps = makePS()
            val dict = fromFile(source, factory = factory)
            ps.println(dict.javaClass)
            walker = PrintWalker(dict, ColorStringifier, ps)
            walkWords(*words)
        }
    }
}
