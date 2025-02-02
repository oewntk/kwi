/*
 * Copyright (c) 2025.
 * Java Wordnet Interface Library (JWI) v2.4.0 Copyright (c) 2007-2015 Mark A. Finlayson
 * Kotlin Wordnet Interface Library (KWI) v1.0 Copyright (c) 2025 Bernard Bou
 * JWI is distributed under the terms of the Creative Commons Attribution 4.0 International Public License
 * KWI is distributed under the terms of the GPL3 License
 */

package org.kwi.item

data class Relation<T>(
    val rel: String,
    val terms: Pair<T, T>,
) {

    override fun toString(): String {
        return "${terms.first} --$rel--> ${terms.second}"
    }
}