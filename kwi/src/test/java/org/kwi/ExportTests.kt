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
import org.kwi.DictionaryFactory.factory
import org.kwi.DictionaryFactory.fromFile
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
