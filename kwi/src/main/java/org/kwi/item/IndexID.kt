/*
 * Copyright (c) 2025.
 * Java Wordnet Interface Library (JWI) v2.4.0 Copyright (c) 2007-2015 Mark A. Finlayson
 * Kotlin Wordnet Interface Library (KWI) v1.0 Copyright (c) 2025 Bernard Bou
 * JWI is distributed under the terms of the Creative Commons Attribution 4.0 International Public License
 * KWI is distributed under the terms of the GPL3 License
 */

package org.kwi.item

import java.util.*

/**
 * A unique identifier / key for an index.
 * An index ID is sufficient to retrieve a specific index from the Wordnet database.
 * It consists of both a lemma and part-of-speech.
 *
 * Constructs an index id object with the specified lemma and part-of-speech.
 * Since all index entries are in lower case, with whitespace converted to underscores, this constructor applies this conversion.
 *
 * @param lemma the lemma for the id
 * @param pOS the part-of-speech for the id
 * @throws IllegalArgumentException if the lemma is empty or all whitespace
 */
class IndexID(
    lemma: String,
    override val pOS: POS,
) : IHasPOS, IItemID {

    /**
     * The lemma (root form) of the sense index that this ID indicates.
     * The lemma will never be empty, or all whitespace.
     */
    val lemma: String = lemma.asEscapedSenseIndexLemma()

    init {
        require(lemma.isNotEmpty())
    }

    override fun hashCode(): Int {
        return Objects.hash(lemma, pOS)
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) {
            return true
        }
        if (obj == null) {
            return false
        }
        if (obj !is IndexID) {
            return false
        }
        val other = obj
        if (lemma != other.lemma) {
            return false
        }
        return pOS == other.pOS
    }

    override fun toString(): String {
        return "$PREFIX$lemma-${pOS.tag}"
    }

    companion object {

        const val PREFIX = "X-"

        /**
         * Convenience method for transforming the result of the toString method into an IndexID
         *
         * @param value the string to be parsed
         * @return the index id
         * @throws IllegalArgumentException if the specified string does not conform to an index id string
         */
        fun parseIndexID(value: String): IndexID {
            require(value.startsWith(PREFIX))

            val pos = POS.getPartOfSpeech(value[value.length - 1])
            return IndexID(value.substring(PREFIX.length, value.length - 2), pos)
        }
    }
}
