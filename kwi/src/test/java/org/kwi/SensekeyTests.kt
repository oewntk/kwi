package org.kwi

import org.kwi.data.parse.SenseKeyParser
import org.kwi.item.LexFile.Companion.NOUN_LOCATION
import org.kwi.item.LexFile.Companion.NOUN_TOPS
import org.kwi.item.POS
import org.kwi.item.SenseKey
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.IOException
import java.io.PrintStream
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class SensekeyTests {

    @Test
    fun sensekeyWithSameOrDifferentLemma() {
        val lemma1 = "entity"
        val lemma2 = "'entity"
        val sk1 = SenseKey(lemma1, POS.NOUN, 0, NOUN_TOPS.number)
        val sk2 = SenseKey(lemma1, POS.NOUN, 0, NOUN_TOPS.number)
        val sk3 = SenseKey(lemma2, POS.NOUN, 0, NOUN_TOPS.number)
        PS.println(sk1)
        PS.println(sk2)
        PS.println(sk3)
        assertNotEquals(lemma1, lemma2)
        assertEquals(sk1, sk2)
        assertNotEquals(lemma1, lemma2)
        assertEquals(sk1.hashCode(), sk2.hashCode())
        assertNotEquals(sk1, sk3)
    }

    @Test
    fun sensekeyDifferingOnCase() {
        val lemma1 = "'s_Gravenhage"
        val lemma2 = "'s_gravenhage"
        val sk1 = SenseKey(lemma1, POS.NOUN, 0, NOUN_LOCATION.number)
        val sk2 = SenseKey(lemma2, POS.NOUN, 0, NOUN_LOCATION.number)
        PS.println(sk1)
        PS.println(sk2)
        assertNotEquals(lemma1, lemma2)
        assertEquals(sk1, sk2)
        assertEquals(sk1.hashCode(), sk2.hashCode())
    }

    @Test
    fun sensekeyDifferingOnCase2() {
        val lemma1 = "Earth"
        val lemma2 = "earth"
        assertNotEquals(lemma1, lemma2)
        val sk1 = SenseKey(lemma1, POS.NOUN, 0, NOUN_LOCATION.number)
        val sk2 = SenseKey(lemma2, POS.NOUN, 0, NOUN_LOCATION.number)
        PS.println(sk1)
        PS.println(sk2)
        assertEquals(sk1, sk2)
        assertEquals(sk1.hashCode(), sk2.hashCode())
    }

    @Test
    fun sensekeyDifferentInstancesEquals() {
        val sk1 = SenseKeyParser.parseLine("earth%1:15:00::")
        val sk2 = SenseKeyParser.parseLine("earth%1:15:00::")
        PS.println(sk1)
        PS.println(sk2)
        assertEquals(sk1, sk2)
        assertEquals(sk1.hashCode(), sk2.hashCode())
    }

    @Test
    fun parseSenseKeys() {
        val sks = listOf(
            "galore%5:00:00:abundant:00",
            "galore%5:00:00:many:00",

            "aborigine%1:18:00::",
            "aborigine%1:18:01::",
            "Aborigine%1:18:00::",
            "Aborigine%1:18:01::",
            "earth%1:15:00::",
            "earth%1:15:01::",

            "a-one%5:00:00:superior:02",
            "0%5:00:00:cardinal:00",
            "zero%5:00:00:cardinal:00",
            "zero%5:00:02:ordinal:00",
            "electric%5:00:00:exciting:00",

            "hot%3:00:01::",
            "hot%3:00:02::",
            "hot%5:00:00:active:01",
            "hot%5:00:00:charged:00",
            "hot%5:00:00:eager:00",
            "hot%5:00:00:fast:01",
            "hot%5:00:00:fresh:01",
            "hot%5:00:00:good:01",
            "hot%5:00:00:illegal:00",
            "hot%5:00:00:lucky:00",
            "hot%5:00:00:near:00",
            "hot%5:00:00:new:00",
            "hot%5:00:00:popular:00",
            "hot%5:00:00:radioactive:00",
            "hot%5:00:00:sexy:00",
            "hot%5:00:00:skilled:00",
            "hot%5:00:00:tasty:00",
            "hot%5:00:00:unpleasant:00",
            "hot%5:00:00:violent:00",
            "hot%5:00:00:wanted:00",

            "you_bet%4:02:00::",
        )
        sks.forEach { s ->
            val sk = SenseKeyParser.parseSenseKey(s)
            PS.println("from    =$s")
            PS.println("sk      =$sk")
            PS.println("sensekey=${sk.sensekey}")
            PS.println("key     =${sk.casedSensekey}")
            PS.println()
            assertNotNull(sk)
            assertIs<SenseKey>(sk)
            assertEquals(s.lowercase(), sk.sensekey)
            assertEquals(s, sk.casedSensekey)
        }
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