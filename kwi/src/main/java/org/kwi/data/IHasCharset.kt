package org.kwi.data

import java.nio.charset.Charset

/**
 * Classes implementing this interface have an associated Charset.
 */
interface IHasCharset {

    val charset: Charset?
}
