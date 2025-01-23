package org.kwi

import org.kwi.DictionaryFactory.fromFile
import org.kwi.DictionaryFactory.makeFactory
import org.kwi.utils.Sequences.seqAllFlatSenseRelations
import org.kwi.utils.Sequences.seqAllFlatSynsetRelations
import org.kwi.utils.Sequences.seqAllLemmas
import org.kwi.utils.Sequences.seqAllSenseEntries
import org.kwi.utils.Sequences.seqAllSenseKeys
import org.kwi.utils.Sequences.seqAllSenseRelations
import org.kwi.utils.Sequences.seqAllSenses
import org.kwi.utils.Sequences.seqAllSynsetRelations
import org.kwi.utils.Sequences.seqAllSynsets
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.IOException
import java.io.PrintStream

class IterateTests {

    @Test
    fun allLemmas() {
        dict.seqAllLemmas().forEach { }
    }

    @Test
    fun allSynsets() {
        dict.seqAllSynsets().forEach { }
    }

    @Test
    fun allSenses() {
        dict.seqAllSenses().forEach { }
    }

    @Test
    fun allSenseEntries() {
        dict.seqAllSenseEntries().forEach { }
    }

    @Test
    fun allSensekeys() {
        dict.seqAllSenseKeys().forEach { }
    }

    @Test
    fun allSynsetRelations() {
        dict.seqAllSynsetRelations().forEach { }
    }

    @Test
    fun allFlatSynsetRelations() {
        dict.seqAllFlatSynsetRelations().forEach { }
    }

    @Test
    fun allSenseRelations() {
        dict.seqAllSenseRelations().forEach { }
    }

    @Test
    fun allFlatSenseRelations() {
        dict.seqAllFlatSenseRelations().forEach { }
    }

    // live

    @Test
    fun allSensekeysAreLive() {
        dict.seqAllSenseKeys().forEach {
            dict.getSense(it)!!
        }
    }

    @Test
    fun allSenseEntriesAreLive() {
        dict.seqAllSenseEntries().forEach {
            try {
                dict.getSense(it.senseKey)!!
            } catch (e: Exception) {
                println(it.senseKey)
                throw e
            }
        }
    }

    @Test
    fun allSenseIDsAreLive() {
        dict.seqAllSenseEntries().forEach {
            try {
                dict.getSense(it.senseKey)!!
            } catch (e: Exception) {
                println(it.senseKey)
                throw e
            }
        }
    }

    fun iterateAll(dict: IDictionary) {
        dict.seqAllLemmas().forEach {}
        dict.seqAllSenses().forEach {}
        dict.seqAllSenseKeys().forEach {}
        dict.seqAllSynsets().forEach {}
        dict.seqAllSenseEntries().forEach {}
        dict.seqAllSenseRelations().forEach {}
        dict.seqAllFlatSenseRelations().forEach{}
        dict.seqAllSynsetRelations().forEach {}
        dict.seqAllFlatSynsetRelations().forEach {}
    }

    companion object {

        private lateinit var dict: IDictionary

        private lateinit var PS: PrintStream

        @JvmStatic
        @BeforeAll
        @Throws(IOException::class)
        fun init() {
            PS = makePS()
            dict = fromFile(System.getProperty("SOURCE"), factory=makeFactory(System.getProperty("FACTORY")))
        }
    }
}