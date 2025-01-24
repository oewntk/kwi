package org.kwi

import org.kwi.DictionaryFactory.fromFile
import org.kwi.DictionaryFactory.factory
import org.kwi.utils.Sequences.seqAllSenseEntries
import org.kwi.utils.Sequences.seqAllSenseKeys
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.IOException
import java.io.PrintStream

class DuplicateTests {

    @Test
    fun allSensekeys() {
        val dups = dict.seqAllSenseKeys().duplicates().toList()
        assert(dups.isEmpty())
    }

    @Test
    fun allSenseEntries() {
        val dups = dict.seqAllSenseEntries().duplicates().toList()
        assert(dups.isEmpty())
    }

    @Test
    fun allSensekeysBySenseKey() {
        val dups = dict.seqAllSenseKeys().duplicatesBy { it.sensekey }.toList()
        assert(dups.isEmpty())
    }

    @Test
    fun allSensekeysByKey() {
        val dups = dict.seqAllSenseKeys().duplicatesBy { it.casedSensekey }.toList()
        assert(dups.isEmpty())
    }

    @Test
    fun allSenseEntriesByKey() {
        val dups = dict.seqAllSenseEntries().duplicatesBy { it.senseKey }.toList()
        assert(dups.isEmpty())
    }

    @Test
    fun allSensekeysSensekeyDiffKey() {
        val diffs = dict.seqAllSenseKeys().filter { it.sensekey != it.casedSensekey }.toList()
        diffs.forEach {
            println("${it.sensekey} ${it.casedSensekey}")
        }
        println("${diffs.size} diffs")
    }

    companion object {

        private lateinit var dict: IDictionary

        private lateinit var PS: PrintStream

        @JvmStatic
        @BeforeAll
        @Throws(IOException::class)
        fun init() {
            PS = makePS()
            dict = fromFile(System.getProperty("SOURCE"), factory=factory(System.getProperty("FACTORY")))
        }
    }
}