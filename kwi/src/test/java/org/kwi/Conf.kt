/*
 * Copyright (c) 2025.
 * Java Wordnet Interface Library (JWI) v2.4.0 Copyright (c) 2007-2015 Mark A. Finlayson
 * Kotlin Wordnet Interface Library (KWI) v1.0 Copyright (c) 2025 Bernard Bou
 * JWI is distributed under the terms of the Creative Commons Attribution 4.0 International Public License
 * KWI is distributed under the terms of the GPL3 License
 */

package org.kwi

import org.kwi.DictionaryFactory.factory
import org.kwi.DictionaryFactory.fromFile
import java.io.OutputStream
import java.io.PrintStream

fun makeDict(wnHomeEnv: String = "SOURCE", config: Config? = null): IDictionary {
    val wnHome = System.getProperty(wnHomeEnv)
    val factory = System.getProperty("FACTORY")
    val configure = System.getProperty("CONFIGURE").toBoolean()
    return fromFile(wnHome, config = if (configure) config else null, factory = factory(factory))
}

private val NULLPS = PrintStream(object : OutputStream() {
    override fun write(b: Int) { /* DO NOTHING */
    }
})

fun makePS(): PrintStream {
    val props = System.getProperties()
    val verbose = !props.containsKey("SILENT")
    return if (verbose) System.out else NULLPS
}

fun <T> measureTimeMillis1(block: () -> T): Pair<Long, T> {
    val startTime = System.currentTimeMillis()
    val result = block()
    val endTime = System.currentTimeMillis()
    return Pair(endTime - startTime, result)
}
