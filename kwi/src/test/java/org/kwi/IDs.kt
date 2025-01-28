package org.kwi

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.kwi.data.parse.SenseKeyParser
import org.kwi.item.*
import org.kwi.item.LexFile.Companion.NOUN_TOPS
import java.io.IOException
import java.io.PrintStream
import kotlin.test.assertEquals

class IDs {

    @Test
    fun parseSenseIDWithNum() {
        val offset = 7777
        val pos = POS.VERB
        val sid = SynsetID(offset, pos)
        val id = SenseIDWithNum(sid, 3)
        PS.println(id)
        val id2 = SenseID.parseSenseID(id.toString())
        assertEquals(id, id2)
    }

    @Test
    fun parseSenseIDWithLemma() {
        val offset = 7777
        val pos = POS.VERB
        val sid = SynsetID(offset, pos)
        val lemma = "abracadabra"
        val id = SenseIDWithLemma(sid, lemma)
        PS.println(id)
        val id2 = SenseID.parseSenseID(id.toString())
        assertEquals(id, id2)
    }

    @Test
    fun parseSenseIDWithLemmaNum() {
        val offset = 7777
        val pos = POS.VERB
        val sid = SynsetID(offset, pos)
        val lemma = "abracadabra"
        val id = SenseIDWithLemmaAndNum(sid, 3, lemma)
        PS.println(id)
        val id2 = SenseID.parseSenseID(id.toString())
        assertEquals(id, id2)
    }

    @Test
    fun parseSynsetID() {
        val offset = 7777
        val pos = POS.VERB
        val sid = SynsetID(offset, pos)
        PS.println(sid)
        val sid2 = SynsetID.parseSynsetID(sid.toString())
        assertEquals(sid, sid2)
    }

    @Test
    fun parseIndexID() {
        val pos = POS.VERB
        val lemma = "abracadabra"
        val idx = IndexID(lemma, pos)
        PS.println(idx)
        val idx2 = IndexID.parseIndexID(idx.toString())
        assertEquals(idx, idx2)
    }

    @Test
    fun parseSenseKey() {
        val pos = POS.VERB
        val lemma = "abracadabra"
        val sk = SenseKey(lemma, pos, 9, NOUN_TOPS.number)
        PS.println(sk)
        val sk2 = SenseKeyParser.parseSenseKey(sk.toString())
        assertEquals(sk, sk2)
    }

    companion object {

        private lateinit var PS: PrintStream

        @JvmStatic
        @BeforeAll
        @Throws(IOException::class)
        fun init() {
            PS = makePS()
        }
    }
}