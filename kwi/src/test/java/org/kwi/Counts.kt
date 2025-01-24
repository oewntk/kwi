package org.kwi

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
import org.kwi.utils.Info.countAll
import java.io.IOException
import java.io.PrintStream

class Counts {

    @Test
    fun count() {
        val c = countAll(dict)
        PS.println(c)
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
