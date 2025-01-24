package org.kwi

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.kwi.DictionaryFactory.fromSer
import org.kwi.utils.Info.info
import java.io.IOException
import java.io.PrintStream
import kotlin.test.assertNotNull

class ImportTests {

    @Test
    fun import() {
        val dict = fromSer(source)
        assertNotNull(dict)
        PS.println(info(dict))
    }

    companion object {

        private lateinit var source: String

        private lateinit var PS: PrintStream

        @JvmStatic
        @BeforeAll
        @Throws(IOException::class)
        fun init() {
            PS = makePS()
            source = System.getProperty("SER") ?: "$source.ser"
        }
    }
}
