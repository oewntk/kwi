package org.kwi

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.kwi.DictionaryFactory.factory
import org.kwi.DictionaryFactory.fromFile
import org.kwi.utils.PrintWalker
import org.kwi.utils.Walker
import java.io.IOException
import java.io.PrintStream
import kotlin.test.assertEquals

class ConfigTests {

    @Test
    @Throws(IOException::class)
    fun testWalk() {
        words.splitToSequence(',').forEach {
            PS.print("\n$charset1:")
            walker1.walk(it)
            PS.print("\n$charset2:")
            walker2.walk(it)
        }
    }

    companion object {

        private lateinit var PS: PrintStream

        private lateinit var words: String

        private var charset1 = Charsets.ISO_8859_1

        private val charset2 = Charsets.UTF_8

        private lateinit var walker1: Walker

        private lateinit var walker2: Walker

        @JvmStatic
        @BeforeAll
        @Throws(IOException::class)
        fun init() {
            words = "señor,Señor" //System.getProperty("WORD")
            PS = makePS()

            val factory = factory("SOURCE")

            val config1 = Config()
            config1.charSet = charset1
            val config2 = Config()
            config2.charSet = charset2

            val dict1 = fromFile(System.getProperty("SOURCE"), config = config1, factory = factory)
            val dsDict1 = dict1 as DataSourceDictionary
            val charset1 = dsDict1.charset
            assertEquals(config1.charSet, charset1)

            val dict2 = fromFile(System.getProperty("SOURCE"), config = config2, factory = factory)
            val dsDict2 = dict2 as DataSourceDictionary
            val charset2 = dsDict2.charset
            assertEquals(config2.charSet, charset2)

            walker1 = PrintWalker(dict1, ColorStringifier, PS)
            walker2 = PrintWalker(dict2, ColorStringifier, PS)
        }
    }
}