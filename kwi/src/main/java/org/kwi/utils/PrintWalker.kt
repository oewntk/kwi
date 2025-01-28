package org.kwi.utils

import org.kwi.IDictionary
import org.kwi.item.POS
import org.kwi.item.Pointer
import org.kwi.item.SenseEntry
import org.kwi.item.SenseID
import org.kwi.item.Synset
import org.kwi.item.VerbFrame
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
        ps.println(str.lemmaToString(lemma))
    }

    override fun consumePos(pos: POS) {
        ps.print(str.posSep())
        ps.println(str.posToString(pos))
    }

    override fun consumePointer(ptr: Pointer) {
        ps.println(str.ptrToString(ptr))
    }

    override fun consumeSenseID(senseid: SenseID) {
        ps.print(str.senseSep())
        ps.println(str.senseIDToString(senseid))
    }

    override fun consumeSynset(synset: Synset) {
        ps.print(str.synsetSep())
        ps.println(str.synsetToString(synset))
    }

    override fun consumeSense(sense: Synset.Sense, senseEntry: SenseEntry?) {
        val se = if (senseEntry != null) " ${str.senseEntryToString(senseEntry)}" else ""
        ps.println("${str.senseToString(sense)}$se")
    }

    override fun consumeRelatedSenseType(pointer: Pointer, level: Int) {
        ps.println(str.relatedTypeToString(pointer, true, level))
    }

    override fun consumeRelatedSynsetType(ptr: Pointer, level: Int) {
        ps.println(str.relatedTypeToString(ptr, false, level))
    }

    override fun consumeRelatedSense(sense: Synset.Sense, pointer: Pointer, level: Int) {
        ps.println(str.relatedSenseToString(sense, pointer))
    }

    override fun consumeRelatedSynset(synset: Synset, ptr: Pointer, level: Int) {
        ps.println(str.relatedSynsetToString(synset, level))
    }

    override fun consumeVerbFrame(verbFrame: VerbFrame, lemma: String) {
        ps.println(str.verbFramesToString(verbFrame, lemma))
    }
}