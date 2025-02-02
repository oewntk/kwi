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
import org.kwi.item.POS
import org.kwi.morph.SimpleStemmer
import org.kwi.morph.SimpleStemmer.Companion.cartesianProduct
import org.kwi.morph.WordnetStemmer
import java.io.IOException
import java.io.PrintStream

class StemsTests {

    @Test
    fun cartesianProductTest() {
        val lists = listOf("ABC".toList(), "123".toList(), "abc".toList())
        val product = cartesianProduct(lists)
        product.forEach { println(it.joinToString(separator = "")) }
    }

    val verbs = arrayOf("works", "does", "finished", "dies")
    val nouns = arrayOf("works", "vertices", "suffixes", "does", "boatsful", "hearts breakers", "mice", "dice", "dies")
    val both = nouns + verbs

    @Test
    fun wordsToStemsTestNoun() {
        stems(POS.NOUN, *nouns)
    }

    @Test
    fun wordsToStemsTestVerb() {
        stems(POS.VERB, *verbs)
    }

    @Test
    fun wordsToStemsTestAll() {
        stems(null, *both)
    }

    @Test
    fun wordsToDictStemsTestNouns() {
        dictStems(POS.NOUN, "boatsful")
        dictStems(POS.NOUN, *nouns)
    }

    @Test
    fun wordsToDictStemsTestVerbs() {
        dictStems(POS.VERB, *verbs)
    }

    @Test
    fun wordsToDictStemsTestAll() {
        dictStems(null, *both)
    }

    companion object {

        private fun stems(pos: POS? = null, vararg words: String) {
            words.forEach {
                val lemmas = stemmer.findStems(it, null)
                PS.println("stems of '$it' for ${pos ?: "any"} are ${lemmas.joinToString(separator = ",", transform = { "'$it'" })}")
            }
        }

        private fun dictStems(pos: POS? = null, vararg words: String) {
            words.forEach {
                val lemmas = dictStemmer.findStems(it, pos)
                PS.println("dict stems of '$it' for ${pos ?: "any"} are ${lemmas.joinToString(separator = ",", transform = { "'$it'" })}")
            }
        }

        private lateinit var PS: PrintStream

        private lateinit var dict: IDictionary

        private lateinit var stemmer: SimpleStemmer

        private lateinit var dictStemmer: WordnetStemmer

        @JvmStatic
        @BeforeAll
        @Throws(IOException::class)
        fun init() {
            dict = makeDict()
            stemmer = SimpleStemmer()
            dictStemmer = WordnetStemmer(dict)
            PS = makePS()
        }
    }
}
