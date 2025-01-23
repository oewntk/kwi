package org.kwi

import org.kwi.DictionaryFactory.fromFile
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.IOException
import java.io.PrintStream

class WalkCrossTests {

    @Test
    fun walkWord() {
        words.splitToSequence(',').forEach {
            PS.println("@".repeat(80))
            PS.println(it)
            PS.println("@".repeat(80))
            walk2(it)
        }
    }

    @Test
    fun walkWord1() {
        words.splitToSequence(',').forEach {
            PS.println("@".repeat(80))
            PS.println(it)
            PS.println("@".repeat(80))
            PS.println(wnHome1)
            walker1.walk(it)
        }
    }

    @Test
    fun walkWord2() {
        words.splitToSequence(',').forEach {
            PS.println("@".repeat(80))
            PS.println(it)
            PS.println("@".repeat(80))
            PS.println(wnHome2)
            walker2.walk(it)
        }
    }

    private fun walk2(lemma: String) {
        PS.println(wnHome1)
        walker1.walk(lemma)
        PS.println(wnHome2)
        walker2.walk(lemma)
    }

    companion object {

        private val PS: PrintStream = makePS()

        private lateinit var words: String

        private lateinit var wnHome1: String

        private lateinit var wnHome2: String

        private lateinit var dict1: IDictionary

        private lateinit var dict2: IDictionary

        private lateinit var walker1: PrintWalker

        private lateinit var walker2: PrintWalker

        @JvmStatic
        @BeforeAll
        @Throws(IOException::class)
        fun init() {
            words = System.getProperty("WORD")
            wnHome1 = System.getProperty("SOURCE")
            wnHome2 = System.getProperty("SOURCE2")
            dict1 = fromFile(wnHome1)
            dict2 = fromFile(wnHome2)
            walker1 = PrintWalker(dict1, ColorStringifier, PS)
            walker2 = PrintWalker(dict2, ColorStringifier, PS)
        }
    }
}
