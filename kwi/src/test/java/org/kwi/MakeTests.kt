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
import org.kwi.utils.Info.countAll
import java.io.IOException
import java.io.PrintStream
import kotlin.system.measureTimeMillis

class MakeTests {

    private lateinit var dict: IDictionary

    @Test
    @Throws(IOException::class)
    fun makeTest() {
        var timeTaken = measureTimeMillis {
            dict = makeDict()
        }
        println("Time taken loading : $timeTaken ms")

        PS.println(dict.version)

        timeTaken = measureTimeMillis {
            PS.println(countAll(dict))
        }
        println("Time taken counting : $timeTaken ms")
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