package org.kwi

import org.kwi.item.*
import org.kwi.item.Synset.Sense
import org.kwi.utils.Walker
import java.io.PrintStream

/**
 * Tree exploration
 *
 * @author Bernard Bou
 */
class PrintWalker(
    dict: IDictionary,
    val str: Stringifier,
    val ps: PrintStream,

    ) : Walker(dict) {

    override fun consumeLemma(lemma: String) {
        ps.print(str.lemmaSep())
        ps.println(str.toString(lemma))
    }

    override fun consumePos(pos: POS) {
        ps.print(str.posSep())
        ps.println(str.toString(pos))
    }

    override fun consumePointer(ptr: Pointer) {
        ps.println(str.toString(ptr))
    }

    override fun consumeSenseID(senseid: SenseID) {
        ps.print(str.senseSep())
        ps.println(str.toString(senseid))
    }

    override fun consumeSynset(synset: Synset) {
        ps.print(str.synsetSep())
        ps.println(str.toString(synset))
    }

    override fun consumeSense(sense: Sense, senseEntry: SenseEntry?) {
        val se = if (senseEntry != null) " ${str.toString(senseEntry)}" else ""
        ps.println("${str.toString(sense)}$se")
    }

    override fun consumeVerbFrame(verbFrame: VerbFrame, lemma: String) {
        ps.println(str.toString(verbFrame, lemma))
    }

    override fun consumeRelatedSenseType(pointer: Pointer, level: Int) {
        ps.println(str.toString(pointer, true, level))
    }

    override fun consumeRelatedSense(sense: Sense, pointer: Pointer, level: Int) {
        ps.println(str.toString(sense, pointer))
    }

    override fun consumeRelatedSynsetType(ptr: Pointer, level: Int) {
        ps.println(str.toString(ptr, false, level))
    }

    override fun consumeRelatedSynset(synset: Synset, ptr: Pointer, level: Int) {
        ps.println(str.toString(synset, level))
    }
}
