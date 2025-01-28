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

    private const val SYNSET = AnsiColors.M
    private const val SENSE = AnsiColors.C
    private const val SYNSETRELATION = AnsiColors.M
    private const val SENSERELATION = AnsiColors.B

    override fun lemmaSep(): String {
        return "\n\n"
    }

    override fun posSep(): String {
        return "\n"
    }

    //override fun senseSep(): String{
    //    return super.senseSep()
    //}

    override fun lemmaToString(lemma: String): String {
        return yellowb(lemma)
    }

    override fun posToString(pos: POS): String {
        return yellow(super.posToString(pos))
    }

    override fun ptrToString(ptr: Pointer): String {
        return black(super.ptrToString(ptr))
    }

    // override fun senseIDToString(senseid: SenseID): String {
    //     return super.toString(senseid)
    // }

    override fun synsetToString(synset: Synset): String {
        return color(SYNSET, super.synsetToString(synset))
    }

    override fun senseToString(sense: Sense): String {
        return color(SENSE, super.senseToString(sense))
    }

    override fun senseEntryToString(senseEntry: SenseEntry): String {
        return color(SENSE, super.senseEntryToString(senseEntry))
    }

    override fun relatedTypeToString(pointer: Pointer, isSense: Boolean, level: Int): String {
        val indentSpace = "\t".repeat(level)
        val text = "$indentSpace🡆 ${bold(pointer.name)}"
        return if (isSense) color(SENSERELATION, text) else color(SYNSETRELATION, text)
    }

    override fun relatedSenseToString(sense: Sense, pointer: Pointer): String {
        return color(SENSERELATION, super.relatedSenseToString(sense, pointer))
    }

    override fun relatedSynsetToString(synset: Synset, level: Int): String {
        return magenta(super.relatedSynsetToString(synset, level))
    }

    override fun verbFramesToString(verbFrame: VerbFrame, lemma: String): String {
        return "  verb frame: ${verbFrame.template} : ${verbFrame.instantiateTemplate(lemma)}"
    }
}
