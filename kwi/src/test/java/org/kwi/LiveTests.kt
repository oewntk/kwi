package org.kwi

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.kwi.DictionaryFactory.fromFile
import org.kwi.item.*
import org.kwi.item.Synset.Sense
import org.kwi.utils.Sequences.seqAllExceptionEntries
import org.kwi.utils.Sequences.seqAllIndexes
import org.kwi.utils.Sequences.seqAllSenseKeys
import org.kwi.utils.Sequences.seqAllSenses
import org.kwi.utils.Sequences.seqAllSynsets
import java.io.IOException
import java.io.PrintStream
import kotlin.test.assertNotNull

class LiveTests {

    @Test
    fun allIndexIDsToIndex() {
        dict.seqAllIndexes().map { it.iD }.forEach {
            assertNotNull(indexIDToIndex(it))
        }
    }

    @Test
    fun allSenseIDsToSense() {
        dict.seqAllSenses().map { it.iD }.forEach {
            assertNotNull(senseIDToSense(it))
        }
    }

    @Test
    fun allSensekeysToSynset() {
        dict.seqAllSenseKeys().forEach {
            assertNotNull(sensekeyToSynset(it))
        }
    }

    @Test
    fun allSensekeysToSense() {
        dict.seqAllSenseKeys().forEach {
            assertNotNull(sensekeyToSense(it))
        }
    }

    @Test
    fun allSynsetIDsToSynset() {
        dict.seqAllSynsets().map { it.iD }.forEach {
            assertNotNull(synsetIDToSynset(it))
        }
    }

    // entries

    @Test
    fun allSensekeysToSenseEntry() {
        dict.seqAllSenseKeys().forEach {
            assertNotNull(sensekeyToSenseEntry(it))
        }
    }

    @Test
    fun allExceptionKeysToExceptionEntry() {
        dict.seqAllExceptionEntries().map { ExceptionKey(surfaceForm = it.surfaceForm, pOS = it.pOS) }.forEach {
            assertNotNull(exceptionEntryIDToExceptionEntry(it))
        }
    }

    companion object {

        fun indexIDToIndex(iid: IndexID): Index? {
            return dict.getIndex(iid)!!
        }

        fun synsetIDToSynset(yid: SynsetID): Synset? {
            return dict.getSynset(yid)!!
        }

        fun senseIDToSense(sid: SenseID): Sense? {
            return dict.getSense(sid)!!
        }

        fun sensekeyToSense(sk: SenseKey): Sense? {
            return dict.getSense(sk)!!
        }

        fun sensekeyToSynset(sensekey: SenseKey): Synset? {
            return dict.getSenseEntry(sensekey)?.let { senseEntry ->
                dict.getSynset(SynsetID(senseEntry.offset, sensekey.pOS))
            }
        }

        fun sensekeyToSenseEntry(sensekey: SenseKey): SenseEntry? {
            return dict.getSenseEntry(sensekey)
        }

        fun exceptionEntryIDToExceptionEntry(exceptionID: ExceptionKey): ExceptionEntry? {
            return dict.getExceptionEntry(exceptionID)
        }

        private lateinit var dict: IDictionary

        private lateinit var PS: PrintStream

        @JvmStatic
        @BeforeAll
        @Throws(IOException::class)
        fun init() {
            PS = makePS()
            dict = fromFile(System.getProperty("SOURCE"))
        }
    }
}