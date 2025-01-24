package org.kwi

import org.kwi.item.*
import org.kwi.item.Synset.Sense
import org.kwi.utils.Sequences.seqMembers

/**
 * Stringify
 *
 * @author Bernard Bou
 */
open class Stringifier {

    open fun lemmaSep(): String {
        return "@".repeat(80)
    }

    open fun posSep(): String {
        return "#".repeat(80)
    }

    open fun senseSep(): String {
        return "-".repeat(80)
    }

    open fun synsetSep(): String {
        return ""
    }

    open fun toString(lemma: String): String {
        return lemma
    }

    open fun toString(pos: POS): String {
        return "■ pos: ${pos.name}"
    }

    open fun toString(ptr: Pointer): String {
        return "\thas $ptr"
    }

    open fun toString(senseid: SenseID): String {
        return ""
    }

    open fun toString(synset: Synset): String {
        return "● synset: ${synset.toShortString()}"
    }

    open fun toString(sense: Sense): String {
        val adjMarker = if (sense.adjectiveMarker != null) " adjmarker=$sense.adjectiveMarker" else ""
        return "● sense: $sense synset=${sense.synset.toShortString()} lexid=${sense.lexicalID} sensekey=${sense.senseKey}$adjMarker"
    }

    open fun toString(senseEntry: SenseEntry): String {
        return "sensenum=${senseEntry.senseNumber} tagcnt=${senseEntry.tagCount}"
    }

    open fun toString(verbFrame: VerbFrame, lemma: String): String {
        return "  verb frame: ${verbFrame.template} : ${verbFrame.instantiateTemplate(lemma)}"
    }

    open fun toString(sense: Sense, pointer: Pointer): String {
        return "  \t'${sense.lemma}' in synset=${sense.synset.toShortString()}"
    }

    open fun toString(pointer: Pointer, isSense: Boolean, level: Int): String {
        val indentSpace = "\t".repeat(level)
        return "$indentSpace🡆 ${pointer.name}"
    }

    open fun toString(synset: Synset, level: Int): String {
        val indentSpace = "\t".repeat(level)
        return "$indentSpace${membersOf(synset)} ${synset.gloss}"
    }

    companion object {

        fun Synset.toShortString(): String {
            return "${this.iD}-${membersOf(this)}"
        }

        fun membersOf(synset: Synset): String {
            return seqMembers(synset).joinToString(separator = ",", prefix = "{", postfix = "}")
        }
    }
}
