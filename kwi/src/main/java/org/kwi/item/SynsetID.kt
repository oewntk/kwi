/*
 * Copyright (c) 2025.
 * Java Wordnet Interface Library (JWI) v2.4.0 Copyright (c) 2007-2015 Mark A. Finlayson
 * Kotlin Wordnet Interface Library (KWI) v1.0 Copyright (c) 2025 Bernard Bou
 * JWI is distributed under the terms of the Creative Commons Attribution 4.0 International Public License
 * KWI is distributed under the terms of the GPL3 License
 */

package org.kwi.item

import org.kwi.item.Synset.Companion.checkOffset
import java.util.*

/**
 * A unique identifier for a synset, sufficient to retrieve it from the Wordnet database.
 * It consists of a part-of-speech and an offset.
 *
 * @param offset the offset
 * @param pOS the part-of-speech
 * @throws IllegalArgumentException if the specified offset is not a legal offset
 */
class SynsetID(
    /**
     * The byte offset for the synset.
     */
    val offset: Int,

    /**
     * The Part Of Speech
     */
    override val pOS: POS,
) : IHasPOS, IItemID {

    init {
        Synset.checkOffset(offset)
    }

    override fun hashCode(): Int {
        return Objects.hash(offset, pOS)
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) {
            return true
        }
        if (obj == null) {
            return false
        }
        if (obj !is SynsetID) {
            return false
        }
        val other = obj
        if (offset != other.offset) {
            return false
        }
        return pOS == other.pOS
    }

    override fun toString(): String {
        return "$PREFIX${zeroFillOffset(offset)}-${pOS.tag.uppercaseChar()}"
    }

    companion object {

        /**
         * String prefix for the toString method.
         */
        const val PREFIX = "S-"

        /**
         * Takes an integer in the closed range [0,99999999] and converts it into an eight decimal digit zero-filled string.
         * E.g., "1" becomes "00000001", "1234" becomes "00001234", and so on.
         * This is used for the generation of synset and sense numbers.
         *
         * @param offset the offset to be converted
         * @return the zero-filled string representation of the offset
         * @throws IllegalArgumentException if the specified offset is not in the valid range of [0,99999999]
         */
        @JvmStatic
        fun zeroFillOffset(offset: Int): String {
            checkOffset(offset)
            return "%08d".format(offset)
        }

        /**
         * Convenience method for transforming the result of the toString method back into an ISynsetID.
         * Synset IDs are always 14 characters long and have the following format:
         * SID-########-C, where
         * ######## is the zero-filled eight decimal digit offset of the synset, and
         * C is the upper-case character code indicating the part-of-speech.
         *
         * @param value the string representation of the id; may include leading or trailing whitespace
         * @return a synset id object corresponding to the specified string representation
         * @throws IllegalArgumentException if the specified string is not a properly formatted synset id
         */
        @JvmStatic
        fun parseSynsetID(value: String): SynsetID {
            var value = value.trim { it <= ' ' }
            require(value.startsWith(PREFIX))

            val synsetFrom = PREFIX.length
            val synsetTo = synsetFrom + 8
            val posFrom = synsetTo + 1
            val posTo = posFrom + 1
            require(value.length >= posTo)

            // get offset
            val offset = value.substring(synsetFrom, synsetTo).toInt()

            // get pos
            val posTag = value[posFrom].lowercaseChar()
            val pos = POS.getPartOfSpeech(posTag)
            requireNotNull(pos) { "unknown part-of-speech tag: $posTag" }
            return SynsetID(offset, pos)
        }
    }
}
