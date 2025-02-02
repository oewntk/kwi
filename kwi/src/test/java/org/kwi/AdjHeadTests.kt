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
import org.kwi.item.SenseIDWithLemma
import org.kwi.item.SynsetID
import java.io.IOException
import java.io.PrintStream

class AdjHeadTests {

    val yi = SynsetID(2190548, POS.ADJECTIVE)

    @Test
    fun head() {
        val y = dict.getSynset(yi)!!
        val hs = y.adjHead
        println("head $hs")
    }

    @Test
    fun headSense() {
        val y = dict.getSynset(yi)!!
        val hsi = y.headSynsetID!!
        val y2 = dict.getSynset(hsi)!!
        val hs = y2.senses[0]
        println("headsense $hs")
    }

    @Test
    fun sensekey() {
        val si = SenseIDWithLemma(yi, "zero")
        val s = dict.getSense(si)!!
        val sk = s.senseKey
        println("headsense $sk")
    }

    companion object {

        private lateinit var PS: PrintStream

        private lateinit var dict: IDictionary

        @JvmStatic
        @BeforeAll
        @Throws(IOException::class)
        fun init() {
            dict = makeDict()
            PS = makePS()
        }
    }
}