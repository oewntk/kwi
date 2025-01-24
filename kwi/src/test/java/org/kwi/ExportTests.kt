package org.kwi

import org.kwi.DictionaryFactory.fromFile
import org.kwi.DictionaryFactory.factory
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.FileOutputStream
import java.io.IOException
import java.io.PrintStream

class ExportTests {

    @Test
    fun export() {
        val d = dict as RAMDictionary
        d.export(FileOutputStream(dest))
    }

    companion object {

        private lateinit var source: String

        private lateinit var dest: String

        private lateinit var PS: PrintStream

        private lateinit var dict: IDictionary

        @JvmStatic
        @BeforeAll
        @Throws(IOException::class)
        fun init() {
            PS = makePS()
            source = System.getProperty("SOURCE")
            dest = System.getProperty("SER") ?: "$source.ser"
            dict = fromFile(source, factory = factory("RAM"))
        }
    }
}
