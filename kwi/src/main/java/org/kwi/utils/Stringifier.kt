package org.kwi.utils

import org.kwi.item.*
import org.kwi.item.Synset.Sense

/**
 * Stringify
 *
 * @author Bernard Bou
 */
open class Stringifier {

    private val width = 80

    open val lemmaSep: CharSequence = "@".repeat(width) + '\n'

    open val posSep: CharSequence  = "#".repeat(width) + '\n'

    open val senseSep: CharSequence = "-".repeat(width) + '\n'

    open val synsetSep: CharSequence = ""

    open val posHeader: CharSequence = "■ "

    open val synsetHeader: CharSequence = "● "

    open val senseHeader: CharSequence = "● "

    open val relationHeader: CharSequence = "→ "

    open val relatedHeader: CharSequence = "- "

    open fun lemmaToCharSequence(lemma: String): CharSequence {
        return lemma
    }

    open fun posToCharSequence(pos: POS): CharSequence {
        return StringBuilder(posHeader)
            .append(pos.name)
    }

    open fun ptrToCharSequence(ptr: Pointer): CharSequence {
        return StringBuilder("\t")
            .append(ptr)
    }

    open fun senseIDToCharSequence(senseid: SenseID): CharSequence {
        return ""
    }

    open fun synsetToCharSequence(synset: Synset): CharSequence {
        return StringBuilder(synsetHeader)
            .append(synset.toShortString())
    }

    open fun senseToCharSequence(sense: Sense): CharSequence {
        val adjMarker = if (sense.adjectiveMarker != null) " adjmarker=$sense.adjectiveMarker" else ""
        return StringBuilder(senseHeader)
            .append(sense.toShortString())
            .append(' ')
            .append("synset=${sense.synset.toShortString()} lexid=${sense.lexicalID} sensekey=${sense.senseKey}$adjMarker")
    }

    open fun senseEntryToCharSequence(senseEntry: SenseEntry): CharSequence {
        return StringBuilder(" ")
            .append("sensenum=${senseEntry.senseNumber} tagcnt=${senseEntry.tagCount}")
    }

    open fun relatedTypeToCharSequence(pointer: Pointer, isSense: Boolean, level: Int): CharSequence {
        val indentSpace = "\t".repeat(level)
        return StringBuilder(indentSpace)
            .append(relationHeader)
            .append(pointer.name)
    }

    open fun relatedSenseToCharSequence(sense: Sense, pointer: Pointer, level: Int): CharSequence {
        val indentSpace = "\t".repeat(level)
        return StringBuilder(indentSpace)
            .append(relatedHeader)
            .append("'${sense.lemma}' in synset=${sense.synset.toShortString()}")
    }

    open fun relatedSynsetToCharSequence(synset: Synset, level: Int): CharSequence {
        val indentSpace = "\t".repeat(level)
        return StringBuilder(indentSpace)
            .append(relatedHeader)
            .append("${membersOf(synset)} ${synset.gloss}")
    }

    open fun verbFramesToCharSequence(verbFrame: VerbFrame, lemma: String): CharSequence {
        return StringBuilder("\t")
            .append("verb frame: ")
            .append("${verbFrame.template}: ${verbFrame.instantiateTemplate(lemma)}")
    }

    companion object {

        fun Sense.toShortString(): CharSequence {
            return "$iD #${iD.senseNumber} '${iD.lemma}'"
        }

        fun Synset.toShortString(): CharSequence {
            return "$iD ${membersOf(this)}"
        }

        fun membersOf(synset: Synset): CharSequence {
            return Sequences.seqMembers(synset).joinToString(separator = ",", prefix = "{", postfix = "}")
        }
    }
}