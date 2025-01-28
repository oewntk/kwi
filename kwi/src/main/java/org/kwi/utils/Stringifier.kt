package org.kwi.utils

import org.kwi.item.POS
import org.kwi.item.Pointer
import org.kwi.item.SenseEntry
import org.kwi.item.SenseID
import org.kwi.item.Synset
import org.kwi.item.VerbFrame

/**
 * Stringify
 *
 * @author Bernard Bou
 */
open class Stringifier {

    open fun lemmaSep(): String {
        return "@".repeat(80) + '\n'
    }

    open fun posSep(): String {
        return "#".repeat(80) + '\n'
    }

    open fun senseSep(): String {
        return "-".repeat(80) + '\n'
    }

    open fun synsetSep(): String {
        return ""
    }

    open fun lemmaToString(lemma: String): String {
        return lemma
    }

    open fun posToString(pos: POS): String {
        return "■ pos: ${pos.name}"
    }

    open fun ptrToString(ptr: Pointer): String {
        return "\thas $ptr"
    }

    open fun senseIDToString(senseid: SenseID): String {
        return ""
    }

    open fun synsetToString(synset: Synset): String {
        return "● synset: ${synset.toShortString()}"
    }

    open fun senseToString(sense: Synset.Sense): String {
        val adjMarker = if (sense.adjectiveMarker != null) " adjmarker=$sense.adjectiveMarker" else ""
        return "● sense: $sense synset=${sense.synset.toShortString()} lexid=${sense.lexicalID} sensekey=${sense.senseKey}$adjMarker"
    }

    open fun senseEntryToString(senseEntry: SenseEntry): String {
        return " sensenum=${senseEntry.senseNumber} tagcnt=${senseEntry.tagCount}"
    }

    open fun relatedTypeToString(pointer: Pointer, isSense: Boolean, level: Int): String {
        val indentSpace = "\t".repeat(level)
        return "$indentSpace🡆 ${pointer.name}"
    }

    open fun relatedSenseToString(sense: Synset.Sense, pointer: Pointer): String {
        return "  \t'${sense.lemma}' in synset=${sense.synset.toShortString()}"
    }

    open fun relatedSynsetToString(synset: Synset, level: Int): String {
        val indentSpace = "\t".repeat(level)
        return "$indentSpace${membersOf(synset)} ${synset.gloss}"
    }

    open fun verbFramesToString(verbFrame: VerbFrame, lemma: String): String {
        return "  verb frame: ${verbFrame.template} : ${verbFrame.instantiateTemplate(lemma)}"
    }

    companion object {

        fun Synset.toShortString(): String {
            return "${this.iD}-${membersOf(this)}"
        }

        fun membersOf(synset: Synset): String {
            return Sequences.seqMembers(synset).joinToString(separator = ",", prefix = "{", postfix = "}")
        }
    }
}