/*
 * Copyright (c) 2025.
 * Java Wordnet Interface Library (JWI) v2.4.0 Copyright (c) 2007-2015 Mark A. Finlayson
 * Kotlin Wordnet Interface Library (KWI) v1.0 Copyright (c) 2025 Bernard Bou
 * JWI is distributed under the terms of the Creative Commons Attribution 4.0 International Public License
 * KWI is distributed under the terms of the GPL3 License
 */

package org.kwi.data.compare

import org.kwi.data.parse.MisformattedLineException

/**
 * A comparator that captures the ordering of lines in Wordnet exception files (e.g., `exc.adv` or `adv.exc` files).
 * These files are ordered alphabetically.
 */
object ExceptionLineComparator : ILineComparator {

    override var commentDetector: ICommentDetector? = null

    override fun compare(line1: String, line2: String): Int {
        val tokens1 = SEPARATOR.split(line1)
        if (tokens1.isEmpty()) {
            throw MisformattedLineException(line1)
        }
        val tokens2 = SEPARATOR.split(line2)
        if (tokens2.isEmpty()) {
            throw MisformattedLineException(line2)
        }
        return tokens1[0].compareTo(tokens2[0])
    }

    private val SEPARATOR: Regex = " ".toRegex()
}
