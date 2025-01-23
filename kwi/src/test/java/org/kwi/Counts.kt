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
import java.io.IOException
import java.io.PrintStream

class Counts {

    @Test
    fun count() {
        countAll(dict, PS)
    }

    companion object {

        @JvmStatic
        fun countAll(dict: IDictionary, ps: PrintStream) {
            val l = dict.seqAllLemmas().count()
            val s = dict.seqAllSenses().count()
            val k = dict.seqAllSenseKeys().count()
            val e = dict.seqAllSenseEntries().count()
            val ry = dict.seqAllSynsetRelations().count()
            val fry = dict.seqAllFlatSynsetRelations().count()
            val y = dict.seqAllSynsets().count()
            val rs = dict.seqAllSenseRelations().count()
            val frs = dict.seqAllFlatSenseRelations().count()
            val report = """
                lemmas           =$l
                senses           =$s
                sensekeys        =$k
                sense entries    =$e
                sense relations  =$rs ($frs flat) 
                synsets          =$y
                synset relations =$ry ($fry flat)
            """.trimIndent()
            ps.println(report)
        }

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
