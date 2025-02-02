/*
 * Copyright (c) 2025.
 * Java Wordnet Interface Library (JWI) v2.4.0 Copyright (c) 2007-2015 Mark A. Finlayson
 * Kotlin Wordnet Interface Library (KWI) v1.0 Copyright (c) 2025 Bernard Bou
 * JWI is distributed under the terms of the Creative Commons Attribution 4.0 International Public License
 * KWI is distributed under the terms of the GPL3 License
 */

package org.kwi

fun <T> Sequence<T>.duplicates(): Sequence<T> {
    return this.groupBy { it }
        .filter { it.value.size != 1 }
        .flatMap { it.value }
        .asSequence()
}

fun <T, K> Sequence<T>.duplicatesBy(transform: (T) -> K): Sequence<T> {
    return this.groupBy { transform(it) }
        .filter { it.value.size != 1 }
        .flatMap { it.value }
        .asSequence()
}