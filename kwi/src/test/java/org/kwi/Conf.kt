package org.kwi

import org.kwi.DictionaryFactory.fromFile
import org.kwi.DictionaryFactory.makeFactory
import java.io.OutputStream
import java.io.PrintStream
import java.nio.charset.Charset

fun makeDict(wnHomeEnv: String = "SOURCE"): IDictionary {
    val wnHome = System.getProperty(wnHomeEnv)
    val factory = System.getProperty("FACTORY")
    val configure = System.getProperty("CONFIGURE").toBoolean()
    val config = Config()
    config.charSet = Charset.defaultCharset()
    return fromFile(wnHome, config = if (configure) config else null, factory = makeFactory(factory))
}

private val NULLPS = PrintStream(object : OutputStream() {
    override fun write(b: Int) { /* DO NOTHING */
    }
})

fun makePS(): PrintStream {
    // val props = System.getProperties()
    val verbose = true // TODO !props.containsKey("SILENT")
    return if (verbose) System.out else NULLPS
}