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
import org.kwi.item.POS
import java.io.IOException
import java.io.PrintStream

class WordStartTests {

    @Test
    fun searchStart() {
        val result: Set<String> = dict.getLemmasStartingWith(start, null, 0)
        PS.println("$start pos=null limit=0: ${result.size}")
        PS.println(result)
    }

    @Test
    fun searchStartLimited() {
        val result: Set<String> = dict.getLemmasStartingWith(start, pos, 3)
        PS.println("$start pos=null limit=3: ${result.size}")
        PS.println(result)
    }

    @Test
    fun searchStartLimitedScoped() {
        val result: Set<String> = dict.getLemmasStartingWith(start, pos, 0)
        PS.println("$start pos=$pos limit=0: ${result.size}")
        PS.println(result)
    }

    companion object {

        private lateinit var dict: IDictionary
        private lateinit var start: String
        private var pos: POS? = null

        private lateinit var PS: PrintStream

        @JvmStatic
        @BeforeAll
        @Throws(IOException::class)
        fun init() {
            PS = makePS()
            dict = fromFile(System.getProperty("SOURCE"), factory = factory(System.getProperty("FACTORY")))
            start = System.getProperty("TARGET")
            val scope = System.getProperty("TARGETSCOPE")
            pos = if (scope != null) POS.valueOf(scope) else null
        }
    }
}