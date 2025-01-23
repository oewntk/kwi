package org.kwi

fun <T> Sequence<T>.duplicates(): Sequence<T> {
    return this.groupBy { it }
        .filter { it.value.size != 1 }
        .flatMap { it.value }
        .asSequence()
}

fun <T,K> Sequence<T>.duplicatesBy(transform: (T)->K): Sequence<T> {
    return this.groupBy { transform(it) }
        .filter { it.value.size != 1 }
        .flatMap { it.value }
        .asSequence()
}