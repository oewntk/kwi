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
import org.kwi.data.parse.DataLineParser
import org.kwi.item.Synset
import java.io.IOException
import java.io.PrintStream
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LexicalIDTests {

    @Test
    fun parseCompatLexicalID() {
        val line = "02504828 00 s 01 hot 03 001 & 02504619 a 0000 | (color) bold and intense; \"hot pink\""
        val compatible = isSynsetLegacyCompatible(line)
        assertTrue(compatible)
    }

    @Test
    fun parseNonCompatLexicalID() {
        val line = "02504828 00 s 01 hot 13 001 & 02504619 a 0000 | (color) bold and intense; \"hot pink\""
        val compatible = isSynsetLegacyCompatible(line)
        assertFalse(compatible)
    }

    companion object {

        @JvmStatic
        private fun isSynsetLegacyCompatible(line: String): Boolean {
            val synset = parse(line)
            synset.senses.forEach {
                PS.println("$it lexid=${it.lexicalID}")
            }
            return synset.senses.map { it.lexicalID }.all { it < 16 }
        }

        @JvmStatic
        private fun parse(line: String): Synset {
            return DataLineParser.parseLine(line)
        }

        private lateinit var PS: PrintStream

        @JvmStatic
        @BeforeAll
        @Throws(IOException::class)
        fun init() {
            PS = makePS()
        }
    }
}