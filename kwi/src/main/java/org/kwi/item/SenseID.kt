package org.kwi.item

import org.kwi.item.SenseIDWithLemmaAndNum.Companion.zeroFillSenseNumber
import org.kwi.item.SynsetID.Companion.zeroFillOffset
import java.util.*

/**
 * Base abstract class containing only reference to synset
 * The other component (the word) is implemented in the derived classes
 *
 * @property synsetID the synset id
 */
abstract class SenseID(
    /**
     * The synset id
     */
    val synsetID: SynsetID,

    ) : IHasPOS, IItemID {

    override val pOS: POS
        get() = synsetID.pOS

    protected fun stubString(): String {
        val pos = synsetID.pOS
        return "$PREFIX${zeroFillOffset(synsetID.offset)}-${pos.tag.uppercaseChar()}"
    }

    companion object {

        const val PREFIX = "s-"

        /**
         * Parses the result of the toString method back into an SenseID.
         * Sense ids are always of the following format: WID-########-P-##-lemma where
         * ######## is the eight decimal digit zero-filled offset of the associated synset,
         * P is the upper case character representing the part-of-speech,
         * ## is the two hexadecimal digit zero-filled sense number (or ?? if unknown), and
         * lemma is the lemma.
         *
         * @param value the string to be parsed
         * @return the parsed id
         * @throws IllegalArgumentException if the specified string does not represent a sense id
         */
        @JvmStatic
        fun parseSenseID(value: String): SenseID {
            require(value.startsWith(PREFIX))

            // get synset id
            val synsetFrom = PREFIX.length
            val synsetTo = synsetFrom + 8
            val posFrom = synsetTo + 1
            val posTo = posFrom + 1
            val numFrom = posTo + 1
            val numTo = numFrom + 2
            val lemmaFrom = numTo + 1
            require(value.length >= lemmaFrom)

            val offset = value.substring(synsetFrom, synsetTo).toInt()
            val pos = POS.getPartOfSpeech(value[posFrom])
            val id = SynsetID(offset, pos)

            // get sense number
            val num = value.substring(numFrom, numTo)
            if (num != SenseIDWithLemma.UNKNOWN_NUMBER) {
                return SenseIDWithNum(id, num.toInt(16))
            }

            // get lemma
            val lemma = value.substring(lemmaFrom)
            require(lemma != SenseIDWithNum.UNKNOWN_LEMMA)
            return SenseIDWithLemma(id, lemma)
        }
    }
}

/**
 * Constructs a sense id from synset id and sense number
 * This constructor produces a sense with a sense number (but without a lemma)
 * The sense number, which is a number from 1 to 255, indicates the order this sense is listed in the Wordnet data files
 *
 * @return an integer between 1 and 255, inclusive
 *
 * @param synsetID the synset id
 * @property senseNumber the sense number
 */
class SenseIDWithNum(synsetID: SynsetID, val senseNumber: Int) : SenseID(synsetID) {

    init {
        Synset.checkSenseNumber(senseNumber)
    }

    override fun hashCode(): Int {
        return Objects.hash(synsetID, senseNumber)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null) {
            return false
        }
        if (other is SenseID && synsetID != other.synsetID)
            return false

        return when (other) {
            is SenseIDWithLemmaAndNum -> senseNumber == other.senseNumber
            is SenseIDWithNum         -> senseNumber == other.senseNumber
            is SenseIDWithLemma       -> false
            else                      -> false
        }
    }

    override fun toString(): String {
        return "${stubString()}-${zeroFillSenseNumber(senseNumber)}-$UNKNOWN_LEMMA"
    }

    companion object {

        const val UNKNOWN_LEMMA: String = "?"
    }
}

/**
 * Constructs a sense id from synset id and lemma
 * This constructor produces a sense id with a lemma
 * The lemma is a non-empty string non-whitespace string
 *
 * @param synsetID the synset id
 * @param lemma lemma arg
 * @property lemma lemma
 * @throws IllegalArgumentException if the lemma is empty or all whitespace
 */
open class SenseIDWithLemma(synsetID: SynsetID, lemma: String) : SenseID(synsetID) {

    val lemma: String = lemma.trim { it <= ' ' }

    init {
        require(lemma.isNotEmpty())
    }

    override fun hashCode(): Int {
        return Objects.hash(synsetID, lemma)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null) {
            return false
        }
        if (other is SenseID && synsetID != other.synsetID)
            return false

        return when (other) {
            is SenseIDWithLemmaAndNum -> lemma.equals(other.lemma, ignoreCase = true)
            is SenseIDWithLemma       -> lemma.equals(other.lemma, ignoreCase = true)
            is SenseIDWithNum         -> false
            else                      -> false
        }
    }

    override fun toString(): String {
        return "${stubString()}-$UNKNOWN_NUMBER-$lemma"
    }

    companion object {

        const val UNKNOWN_NUMBER: String = "??"
    }
}

/**
 * Constructs a sense id from the specified arguments.
 * This constructor produces a sense id with a sense number and a lemma
 * The sense number, which is a number from 1 to 255, indicates the order this sense is listed in the Wordnet data files
 * The lemma is a non-empty string non-whitespace string
 *
 * @param synsetID the synset id
 * @property senseNumber the sense number
 * @param lemma the lemma; may not be empty or all whitespace
 * @throws IllegalArgumentException if the lemma is empty or all whitespace
 */
class SenseIDWithLemmaAndNum(synsetID: SynsetID, val senseNumber: Int, lemma: String) : SenseIDWithLemma(synsetID, lemma) {

    init {
        Synset.checkSenseNumber(senseNumber)
    }

    override fun hashCode(): Int {
        return Objects.hash(synsetID, senseNumber, lemma)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null) {
            return false
        }
        if (other is SenseID && synsetID != other.synsetID)
            return false

        return when (other) {
            is SenseIDWithLemmaAndNum -> lemma.equals(other.lemma, ignoreCase = true) && senseNumber == other.senseNumber
            is SenseIDWithLemma       -> lemma.equals(other.lemma, ignoreCase = true)
            is SenseIDWithNum         -> senseNumber == other.senseNumber
            else                      -> false
        }
    }

    override fun toString(): String {
        return "${stubString()}-${zeroFillSenseNumber(senseNumber)}-$lemma"
    }

    companion object {

        /**
         * Returns a string representation of the specified integer as a two hex digit zero-filled string.
         * E.g., "1" becomes "01", "10" becomes "0A", and so on.
         * This is used for the generation of Sense ID numbers.
         *
         * @param num the number to be converted
         * @return a two hex digit zero-filled string representing the specified number
         * @throws IllegalArgumentException if the specified number is not a legal sense number
         */
        @JvmStatic
        fun zeroFillSenseNumber(num: Int): String {
            return "%02x".format(num)
        }
    }
}
