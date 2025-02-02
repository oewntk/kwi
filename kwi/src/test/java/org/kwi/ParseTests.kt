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
import org.kwi.data.parse.DataLineParser
import org.kwi.item.Synset
import java.io.IOException
import java.io.PrintStream
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class ParseTests {

    val lines = listOf(
        "00001740 03 n 01 entity 0 008 ~ 00002020 n 0000 ~ 00002227 n 0000 ~ 05109194 n 0000 ` 03450533 v 0101 ` 01448335 v 0101 ` 00899866 v 0101 ` 01455045 v 0101 ` 02700295 v 0101 | that which is perceived or known or inferred to have its own distinct existence (living or nonliving)",
        "00001740 29 v 04 breathe 0 take_a_breath 0 respire 0 suspire 3 032 @ 03323987 v 0000 ~ 00002772 v 0000 ~ 00002923 v 0000 ~ 00003179 v 0000 ~ 00004191 v 0000 ~ 00004398 v 0000 ~ 00004594 v 0000 ~ 00005703 v 0000 ~ 00007618 v 0000 ~ 00008324 v 0000 ~ 00021189 v 0000 * 00005703 v 0000 * 00004594 v 0000 $ 00002524 v 0000 $ 00002772 v 0000 ^ 00004594 v 0103 ^ 00005703 v 0103 + 04908475 n 0105 + 01025362 n 0101 ` 17073606 n 0101 ` 01029547 n 0101 ` 00768081 n 0101 ` 08851091 n 0101 ` 17068817 n 0101 ` 02876224 n 0101 ` 06481318 n 0101 ` 16864157 n 0101 ` 12236460 n 0101 ` 06413947 n 0101 + 03117444 a 0301 + 01025362 n 0303 + 04709554 n 0301 02 + 02 00 + 08 00 | draw air into, and expel out of, the lungs; \"I can breathe better when the air is clean.\" \"The patient is respiring.\"",
        "00001740 00 a 01 able 0 005 = 06026773 n 0000 = 06505125 n 0000 ! 00002101 a 0101 + 06505125 n 0101 + 06026773 n 0101 | (usually followed by ‘to’) having the necessary means or skill or know-how or authority to do something; \"able to swim\" \"She was able to program her computer.\" \"We were at last able to buy a car.\" \"able to get a grant for the project\"",
        "00001740 02 r 02 a_cappella 0 a_capella 0 002 \\ 02256393 s 0101 \\ 02256393 s 0202 | without musical accompaniment; \"They performed a cappella.\""
    )

    @Test
    fun parseDataLine() {
        lines.forEach {
            val synset: Synset = DataLineParser.parseLine(it)
            PS.println(synset)
            assertNotNull(synset)
            assertIs<Synset>(synset)
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