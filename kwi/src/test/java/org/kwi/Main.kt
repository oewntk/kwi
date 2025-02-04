/*
 * Copyright (c) 2025.
 * Java Wordnet Interface Library (JWI) v2.4.0 Copyright (c) 2007-2015 Mark A. Finlayson
 * Kotlin Wordnet Interface Library (KWI) v1.0 Copyright (c) 2025 Bernard Bou
 * JWI is distributed under the terms of the Creative Commons Attribution 4.0 International Public License
 * KWI is distributed under the terms of the GPL3 License
 */

package org.kwi

import org.kwi.DictionaryFactory.fromFile
import org.kwi.data.FileProvider
import org.kwi.data.LoadPolicy.IMMEDIATE_LOAD
import org.kwi.utils.PrintWalker
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

        val defaultFactory: (url: URL, config: Config?) -> IDictionary = { url: URL, config: Config? -> Dictionary(url, config) }
        val nonCachingFactory: (url: URL, config: Config?) -> IDictionary = { url: URL, config: Config? -> DataSourceDictionary(FileProvider(url), config) }
        val ramFactory: (url: URL, config: Config?) -> IDictionary = { url: URL, config: Config? -> RAMDictionary(url, IMMEDIATE_LOAD, config) }

        private lateinit var walker: Walker

        private var factory: (url: URL, config: Config?) -> IDictionary = defaultFactory

        fun walkWords(vararg words: String) {
            words.forEach {
                walker.walkTop(it)
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
