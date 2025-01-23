package org.kwi

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.kwi.DictionaryFactory.fromFile
import org.kwi.DictionaryFactory.makeFactory
import org.kwi.utils.Walker
import java.io.IOException

/**
 * Tree exploration
 *
 * @author Bernard Bou
 */
class WalkTests {

    @Test
    fun walkTest() {
        words.splitToSequence(',').forEach {
            walker.walk(it)
        }
    }

    companion object {

        private lateinit var words: String

        private lateinit var walker: Walker

        @JvmStatic
        @BeforeAll
        @Throws(IOException::class)
        fun init() {
            words = System.getProperty("WORD")
            val ps = makePS()
            val dict = fromFile(System.getProperty("SOURCE"), factory = makeFactory(System.getProperty("FACTORY")))
            walker = PrintWalker(dict, ColorStringifier, ps)
        }
    }
}
