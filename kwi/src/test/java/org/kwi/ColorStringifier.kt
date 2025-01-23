package org.kwi

import org.kwi.AnsiColors.black
import org.kwi.AnsiColors.bold
import org.kwi.AnsiColors.color
import org.kwi.AnsiColors.magenta
import org.kwi.AnsiColors.yellow
import org.kwi.AnsiColors.yellowb
import org.kwi.item.*
import org.kwi.item.Synset.Sense

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

    override fun toString(lemma: String): String {
        return yellowb(lemma)
    }

    override fun toString(pos: POS): String {
        return yellow(super.toString(pos))
    }

    override fun toString(ptr: Pointer): String {
        return black(super.toString(ptr))
    }

    // override fun toString(senseid: SenseID): String {
    //     return super.toString(senseid)
    // }

    override fun toString(synset: Synset): String {
        return color(SYNSET, super.toString(synset))
    }

    override fun toString(sense: Sense): String {
        return color(SENSE, super.toString(sense))
    }

    override fun toString(senseEntry: SenseEntry): String {
        return color(SENSE, super.toString(senseEntry))
    }

    override fun toString(sense: Sense, pointer: Pointer): String {
        return color(SENSERELATION, super.toString(sense, pointer))
    }

    override fun toString(pointer: Pointer, isSense: Boolean, level: Int): String {
        val indentSpace = "\t".repeat(level)
        val text = "$indentSpace🡆 ${bold(pointer.name)}"
        return if (isSense) color(SENSERELATION, text) else color(SYNSETRELATION, text)
    }

    override fun toString(synset: Synset, level: Int): String {
        return magenta(super.toString(synset, level))
    }

    override fun toString(verbFrame: VerbFrame, lemma: String): String {
        return "  verb frame: ${verbFrame.template} : ${verbFrame.instantiateTemplate(lemma)}"
    }
}
