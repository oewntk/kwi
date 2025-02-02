/*
 * Copyright (c) 2025.
 * Java Wordnet Interface Library (JWI) v2.4.0 Copyright (c) 2007-2015 Mark A. Finlayson
 * Kotlin Wordnet Interface Library (KWI) v1.0 Copyright (c) 2025 Bernard Bou
 * JWI is distributed under the terms of the Creative Commons Attribution 4.0 International Public License
 * KWI is distributed under the terms of the GPL3 License
 */

package org.kwi

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.kwi.DictionaryFactory.factory
import org.kwi.DictionaryFactory.fromFile
import org.kwi.utils.PrintWalker
import org.kwi.utils.Walker
import java.io.IOException

/**
 * Tree exploration
 *
 * @author Bernard Bou
 */
class WalkTests {

    @Test
    fun walkTest() {
        words.splitToSequence(',').forEach {
            walker.walkTop(it)
        }
    }

    companion object {

        private lateinit var words: String

        private lateinit var walker: Walker

        @JvmStatic
        @BeforeAll
        @Throws(IOException::class)
        fun init() {
            words = System.getProperty("WORD")
            val ps = makePS()
            val dict = fromFile(System.getProperty("SOURCE"), factory = factory(System.getProperty("FACTORY")))
            walker = PrintWalker(dict, ColorStringifier, ps)
        }
    }
}
