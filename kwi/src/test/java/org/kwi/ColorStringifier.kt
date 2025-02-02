/*
 * Copyright (c) 2025.
 * Java Wordnet Interface Library (JWI) v2.4.0 Copyright (c) 2007-2015 Mark A. Finlayson
 * Kotlin Wordnet Interface Library (KWI) v1.0 Copyright (c) 2025 Bernard Bou
 * JWI is distributed under the terms of the Creative Commons Attribution 4.0 International Public License
 * KWI is distributed under the terms of the GPL3 License
 */

package org.kwi

import org.kwi.AnsiColors.black
import org.kwi.AnsiColors.bold
import org.kwi.AnsiColors.color
import org.kwi.AnsiColors.magenta
import org.kwi.AnsiColors.yellow
import org.kwi.AnsiColors.yellowb
import org.kwi.item.*
import org.kwi.item.Synset.Sense
import org.kwi.utils.Stringifier

/**
 * Stringify
 *
 * @author Bernard Bou
 */
object ColorStringifier : Stringifier() {

    private const val SYNSET = AnsiColors.K + AnsiColors.bM
    private const val SYNSET_RELATION = AnsiColors.M
    private const val SENSE = AnsiColors.K + AnsiColors.bB
    private const val SENSE_RELATION = AnsiColors.B

    override val lemmaSep: CharSequence = "\n\n"

    override val posSep: CharSequence = "\n"

    override val senseSep: CharSequence  = super.senseSep

    override fun lemmaToCharSequence(lemma: String): CharSequence {
        return yellowb(lemma)
    }

    override fun posToCharSequence(pos: POS): CharSequence {
        return yellow(super.posToCharSequence(pos))
    }

    override fun ptrToCharSequence(ptr: Pointer): CharSequence {
        return black(super.ptrToCharSequence(ptr))
    }

    // override fun senseIDToCharSequence(senseid: SenseID): CharSequence {
    //     return super.senseIDToCharSequence(senseid)
    // }

    override fun synsetToCharSequence(synset: Synset): CharSequence {
        return color(SYNSET, super.synsetToCharSequence(synset))
    }

    override fun senseToCharSequence(sense: Sense): CharSequence {
        return color(SENSE, super.senseToCharSequence(sense))
    }

    override fun senseEntryToCharSequence(senseEntry: SenseEntry): CharSequence {
        return color(SENSE, super.senseEntryToCharSequence(senseEntry))
    }

    override fun relatedTypeToCharSequence(pointer: Pointer, isSense: Boolean, level: Int): CharSequence {
        val indentSpace = "\t".repeat(level)
        val text = "$indentSpace$relationHeader ${bold(pointer.name)}"
        return if (isSense) color(SENSE_RELATION, text) else color(SYNSET_RELATION, text)
    }

    override fun relatedSenseToCharSequence(sense: Sense, pointer: Pointer, level: Int): CharSequence {
        return color(SENSE_RELATION, super.relatedSenseToCharSequence(sense, pointer, level))
    }

    override fun relatedSynsetToCharSequence(synset: Synset, level: Int): CharSequence {
        return magenta(super.relatedSynsetToCharSequence(synset, level))
    }

    // override fun verbFramesToCharSequence(verbFrame: VerbFrame, lemma: String): CharSequence {
    //     return super.verbFramesToCharSequence(verbFrame, lemma)
    // }
}
