package org.kwi

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
