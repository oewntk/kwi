package org.kwi.item

import java.io.Serializable

/**
 * This class includes, as public fields, all pointers, lexical and semantic, defined in the standard WordNet distribution.
 *
 * This class in not implemented as an `Enum` so that clients may instantiate their own pointers using this implementation.
 */
class Pointer(
    symbol: String,
    name: String,
) : Serializable {

    val symbol: String = checkString(symbol)

    val name: String = checkString(name)

    override fun toString(): String {
        return name
    }

    companion object {

        @JvmStatic val ALSO_SEE: Pointer = Pointer("^", "Also See")
        @JvmStatic val ANTONYM: Pointer = Pointer("!", "Antonym")
        @JvmStatic val ATTRIBUTE: Pointer = Pointer("=", "Attribute")
        @JvmStatic val CAUSE: Pointer = Pointer(">", "Cause")
        @JvmStatic val DERIVATIONALLY_RELATED: Pointer = Pointer("+", "Derivation")
        @JvmStatic val DERIVED_FROM_ADJ: Pointer = Pointer("\\", "Derived from adjective")
        @JvmStatic val DOMAIN: Pointer = Pointer(";", "Domain of synset (undifferentiated)")
        @JvmStatic val ENTAILMENT: Pointer = Pointer("*", "Entailment")
        @JvmStatic val HYPERNYM: Pointer = Pointer("@", "Hypernym")
        @JvmStatic val HYPERNYM_INSTANCE: Pointer = Pointer("@i", "Instance hypernym")
        @JvmStatic val HYPONYM: Pointer = Pointer("~", "Hyponym")
        @JvmStatic val HYPONYM_INSTANCE: Pointer = Pointer("~i", "Instance hyponym")
        @JvmStatic val HOLONYM_MEMBER: Pointer = Pointer("#m", "Member holonym")
        @JvmStatic val HOLONYM_SUBSTANCE: Pointer = Pointer("#s", "Substance holonym")
        @JvmStatic val HOLONYM_PART: Pointer = Pointer("#p", "Part holonym")
        @JvmStatic val MEMBER: Pointer = Pointer("-", "Member of this domain (undifferentiated)")
        @JvmStatic val MERONYM_MEMBER: Pointer = Pointer("%m", "Member meronym")
        @JvmStatic val MERONYM_SUBSTANCE: Pointer = Pointer("%s", "Substance meronym")
        @JvmStatic val MERONYM_PART: Pointer = Pointer("%p", "Part meronym")
        @JvmStatic val PARTICIPLE: Pointer = Pointer("<", "Participle")
        @JvmStatic val PERTAINYM: Pointer = Pointer("\\", "Pertainym (pertains to nouns)")
        @JvmStatic val REGION: Pointer = Pointer(";r", "Domain of synset - REGION")
        @JvmStatic val REGION_MEMBER: Pointer = Pointer("-r", "Member of this domain - REGION")
        @JvmStatic val SIMILAR_TO: Pointer = Pointer("&", "Similar To")
        @JvmStatic val TOPIC: Pointer = Pointer(";c", "Domain of synset - TOPIC")
        @JvmStatic val TOPIC_MEMBER: Pointer = Pointer("-c", "Member of this domain - TOPIC")
        @JvmStatic val USAGE: Pointer = Pointer(";u", "Domain of synset - USAGE")
        @JvmStatic val USAGE_MEMBER: Pointer = Pointer("-u", "Member of this domain - USAGE")
        @JvmStatic val VERB_GROUP: Pointer = Pointer("$", "Verb Group")
        @JvmStatic val IS_CAUSED: Pointer = Pointer(">^", "Is caused by")
        @JvmStatic val IS_ENTAILED: Pointer = Pointer("*^", "Is entailed by")
        @JvmStatic val COLLOCATION: Pointer = Pointer("`", "Collocation")

        /**
         * Throws an exception if the specified string empty, or all whitespace. Returns a trimmed form of the string.
         *
         * @param str the string to be checked
         * @return a trimmed form of the string
         * @throws IllegalArgumentException if the specified string is empty or all whitespace
         */
        private fun checkString(str: String): String {
            var str = str.trim { it <= ' ' }
            require(str.isNotEmpty())
            return str
        }

        private val pointerSet: Set<Pointer> = setOf(
            ALSO_SEE,
            ANTONYM,
            ATTRIBUTE,
            CAUSE,
            DERIVATIONALLY_RELATED,
            DERIVED_FROM_ADJ,
            DOMAIN,
            ENTAILMENT,
            HYPERNYM,
            HYPERNYM_INSTANCE,
            HYPONYM,
            HYPONYM_INSTANCE,
            HOLONYM_MEMBER,
            HOLONYM_SUBSTANCE,
            HOLONYM_PART,
            MEMBER,
            MERONYM_MEMBER,
            MERONYM_SUBSTANCE,
            MERONYM_PART,
            PARTICIPLE,
            PERTAINYM,
            REGION,
            REGION_MEMBER,
            SIMILAR_TO,
            TOPIC,
            TOPIC_MEMBER,
            USAGE,
            USAGE_MEMBER,
            VERB_GROUP,
            IS_CAUSED,
            IS_ENTAILED,
            COLLOCATION,
        )

        private val pointerMap: Map<String, Pointer> = pointerSet.asSequence()
            .filterNot { it == DERIVED_FROM_ADJ }
            .map { it.symbol to it }.toMap()

        /**
         * Emulates the `Enum#values()` function. Returns an unmodifiable collection of all the pointers declared in this class, in the order they are declared.
         *
         * @return returns an unmodifiable collection of the pointers declared in this class
         */
        fun values(): Collection<Pointer> {
            return pointerSet
        }

        private const val AMBIGUOUS_SYMBOL = "\\"

        /**
         * Returns the pointer type (static final instance) that matches the
         * specified pointer symbol.
         *
         * @param symbol the symbol to look up
         * @param pos the part-of-speech for the symbol
         * except for ambiguous symbols @return pointer
         * @throws IllegalArgumentException if the symbol does not correspond to a known pointer.
         */
        @JvmStatic
        fun getPointerType(symbol: String, pos: POS?): Pointer {
            if (pos == POS.ADVERB && symbol == AMBIGUOUS_SYMBOL) {
                return DERIVED_FROM_ADJ
            }
            val pointerType: Pointer = pointerMap[symbol]!!
            requireNotNull(pointerType) { "No pointer type corresponding to symbol '$symbol'" }
            return pointerType
        }

        @JvmStatic
        fun canRecurse(p: Pointer): Boolean {
            when (p.symbol) {
                "@", "~", "%p", "#p", "%m", "#m", "%s", "#s", "*", ">" -> return true
            }
            return false
        }
    }
}
