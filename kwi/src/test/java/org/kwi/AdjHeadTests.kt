package org.kwi

import org.kwi.item.POS
import org.kwi.item.SenseIDWithLemma
import org.kwi.item.SynsetID
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
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