package org.kwi.utils

import org.kwi.IDictionary
import org.kwi.item.*

/**
 * Tree exploration
 *
 * @author Bernard Bou
 */
open class BuilderWalker(
    dict: IDictionary,
    val str: Stringifier,
) : Walker(dict) {

    val sb = StringBuilder()

    override fun consumeLemma(lemma: String) {
        sb.append(str.lemmaSep())
        sb.append(str.lemmaToCharSequence(lemma)).append('\n')
    }

    override fun consumePos(pos: POS) {
        sb.append(str.posSep())
        sb.append(str.posToCharSequence(pos)).append('\n')
    }

    override fun consumePointer(ptr: Pointer) {
        sb.append(str.ptrToCharSequence(ptr)).append('\n')
    }

    override fun consumeSenseID(senseid: SenseID) {
        sb.append(str.senseSep())
        sb.append(str.senseIDToCharSequence(senseid))
    }

    override fun consumeSynset(synset: Synset) {
        sb.append(str.synsetSep())
        sb.append(str.synsetToCharSequence(synset)).append('\n')
    }

    override fun consumeSense(sense: Synset.Sense, senseEntry: SenseEntry?) {
        val se = if (senseEntry != null) str.senseEntryToCharSequence(senseEntry) else ""
        sb.append("${str.senseToCharSequence(sense)}$se").append('\n')
    }

    override fun consumeRelatedSenseType(pointer: Pointer, level: Int) {
        sb.append(str.relatedTypeToCharSequence(pointer, true, level)).append('\n')
    }

    override fun consumeRelatedSynsetType(ptr: Pointer, level: Int) {
        sb.append(str.relatedTypeToCharSequence(ptr, false, level)).append('\n')
    }

    override fun consumeRelatedSense(sense: Synset.Sense, pointer: Pointer, level: Int) {
        sb.append(str.relatedSenseToCharSequence(sense, pointer)).append('\n')
    }

    override fun consumeRelatedSynset(synset: Synset, ptr: Pointer, level: Int) {
        sb.append(str.relatedSynsetToCharSequence(synset, level)).append('\n')
    }

    override fun consumeVerbFrame(verbFrame: VerbFrame, lemma: String) {
        sb.append(str.verbFramesToCharSequence(verbFrame, lemma)).append('\n')
    }
}