/*
 * Copyright (c) 2025.
 * Java Wordnet Interface Library (JWI) v2.4.0 Copyright (c) 2007-2015 Mark A. Finlayson
 * Kotlin Wordnet Interface Library (KWI) v1.0 Copyright (c) 2025 Bernard Bou
 * JWI is distributed under the terms of the Creative Commons Attribution 4.0 International Public License
 * KWI is distributed under the terms of the GPL3 License
 */

package org.kwi.item

import java.util.regex.Pattern

// sense keys

fun String.asSensekeyLemma(): String {
    return this.lowercase()
}

// indexes

private val whitespace: Pattern = Pattern.compile("\\s+")

fun String.asIndexLemma(): String {
    return this.lowercase().trim { it <= ' ' }
}

fun String.asEscapedSenseIndexLemma(): String {
    return whitespace.matcher(this.asIndexLemma()).replaceAll("_")
}

// exceptions

fun String.asSurfaceForm(): String {
    return this.lowercase().trim { it <= ' ' }
}
