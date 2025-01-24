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