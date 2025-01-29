package org.kwi.utils

import org.kwi.item.*

/**
 * Stringify
 *
 * @author Bernard Bou
 */
open class Stringifier {

    open val posBullet: CharSequence = "■ "

    open val synsetBullet: CharSequence = "● "

    open val senseBullet: CharSequence = "● "

    open val relationBullet: CharSequence = "→ "

    open val relatedBullet: CharSequence = "- "

    open fun lemmaSep(): CharSequence {
        return "@".repeat(80) + '\n'
    }

    open fun posSep(): CharSequence {
        return "#".repeat(80) + '\n'
    }

    open fun senseSep(): CharSequence {
        return "-".repeat(80) + '\n'
    }

    open fun synsetSep(): CharSequence {
        return ""
    }

    open fun lemmaToCharSequence(lemma: String): CharSequence {
        return lemma
    }

    open fun posToCharSequence(pos: POS): CharSequence {
        return StringBuilder(posBullet)
            .append("pos: ")
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
        return StringBuilder(synsetBullet)
            .append("synset: ")
            .append(synset.toShortString())
    }

    open fun senseToCharSequence(sense: Synset.Sense): CharSequence {
        val adjMarker = if (sense.adjectiveMarker != null) " adjmarker=$sense.adjectiveMarker" else ""
        return StringBuilder(senseBullet)
            .append("sense: ")
            .append(sense)
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
            .append(relationBullet)
            .append(pointer.name)
    }

    open fun relatedSenseToCharSequence(sense: Synset.Sense, pointer: Pointer, level: Int): CharSequence {
        val indentSpace = "\t".repeat(level)
        return StringBuilder(indentSpace)
            .append(relatedBullet)
            .append("'${sense.lemma}' in synset=${sense.synset.toShortString()}")
    }

    open fun relatedSynsetToCharSequence(synset: Synset, level: Int): CharSequence {
        val indentSpace = "\t".repeat(level)
        return StringBuilder(indentSpace)
            .append(relatedBullet)
            .append("${membersOf(synset)} ${synset.gloss}")
    }

    open fun verbFramesToCharSequence(verbFrame: VerbFrame, lemma: String): CharSequence {
        return StringBuilder("\t")
            .append("verb frame: ")
            .append("${verbFrame.template}: ${verbFrame.instantiateTemplate(lemma)}")
    }

    companion object {

        fun Synset.toShortString(): CharSequence {
            return "${this.iD}-${membersOf(this)}"
        }

        fun membersOf(synset: Synset): CharSequence {
            return Sequences.seqMembers(synset).joinToString(separator = ",", prefix = "{", postfix = "}")
        }
    }
}