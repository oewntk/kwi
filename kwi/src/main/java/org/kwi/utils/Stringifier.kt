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

    open val posBullet = "■"

    open val synsetBullet = "●"

    open val senseBullet = "●"

    open val relationBullet = "→"

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
        return "$posBullet pos: ${pos.name}"
    }

    open fun ptrToCharSequence(ptr: Pointer): CharSequence {
        return "\thas $ptr"
    }

    open fun senseIDToCharSequence(senseid: SenseID): CharSequence {
        return ""
    }

    open fun synsetToCharSequence(synset: Synset): CharSequence {
        return "$synsetBullet synset: ${synset.toShortString()}"
    }

    open fun senseToCharSequence(sense: Synset.Sense): CharSequence {
        val adjMarker = if (sense.adjectiveMarker != null) " adjmarker=$sense.adjectiveMarker" else ""
        return "$senseBullet sense: $sense synset=${sense.synset.toShortString()} lexid=${sense.lexicalID} sensekey=${sense.senseKey}$adjMarker"
    }

    open fun senseEntryToCharSequence(senseEntry: SenseEntry): CharSequence {
        return " sensenum=${senseEntry.senseNumber} tagcnt=${senseEntry.tagCount}"
    }

    open fun relatedTypeToCharSequence(pointer: Pointer, isSense: Boolean, level: Int): CharSequence {
        val indentSpace = "\t".repeat(level)
        return "$indentSpace$relationBullet ${pointer.name}"
    }

    open fun relatedSenseToCharSequence(sense: Synset.Sense, pointer: Pointer): CharSequence {
        return "\t'${sense.lemma}' in synset=${sense.synset.toShortString()}"
    }

    open fun relatedSynsetToCharSequence(synset: Synset, level: Int): CharSequence {
        val indentSpace = "\t".repeat(level)
        return "$indentSpace${membersOf(synset)} ${synset.gloss}"
    }

    open fun verbFramesToCharSequence(verbFrame: VerbFrame, lemma: String): CharSequence {
        return "\tverb frame: ${verbFrame.template} : ${verbFrame.instantiateTemplate(lemma)}"
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