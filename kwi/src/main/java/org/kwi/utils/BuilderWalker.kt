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
    val builder: Appendable,
) : Walker(dict) {


    override fun consumeLemma(lemma: String) {
        builder.append(str.lemmaSep())
        builder.append(str.lemmaToCharSequence(lemma)).append('\n')
    }

    override fun consumePos(pos: POS) {
        builder.append(str.posSep())
        builder.append(str.posToCharSequence(pos)).append('\n')
    }

    override fun consumePointer(ptr: Pointer) {
        builder.append(str.ptrToCharSequence(ptr)).append('\n')
    }

    override fun consumeSenseID(senseid: SenseID) {
        builder.append(str.senseSep())
        builder.append(str.senseIDToCharSequence(senseid))
    }

    override fun consumeSynset(synset: Synset) {
        builder.append(str.synsetSep())
        builder.append(str.synsetToCharSequence(synset)).append('\n')
    }

    override fun consumeSense(sense: Synset.Sense, senseEntry: SenseEntry?) {
        val se = if (senseEntry != null) str.senseEntryToCharSequence(senseEntry) else ""
        builder.append(str.senseToCharSequence(sense)).append(se).append('\n')
    }

    override fun consumeRelatedSenseType(pointer: Pointer, level: Int) {
        builder.append(str.relatedTypeToCharSequence(pointer, true, level)).append('\n')
    }

    override fun consumeRelatedSynsetType(ptr: Pointer, level: Int) {
        builder.append(str.relatedTypeToCharSequence(ptr, false, level)).append('\n')
    }

    override fun consumeRelatedSense(sense: Synset.Sense, pointer: Pointer, level: Int) {
        builder.append(str.relatedSenseToCharSequence(sense, pointer, level)).append('\n')
    }

    override fun consumeRelatedSynset(synset: Synset, ptr: Pointer, level: Int) {
        builder.append(str.relatedSynsetToCharSequence(synset, level)).append('\n')
    }

    override fun consumeVerbFrame(verbFrame: VerbFrame, lemma: String) {
        builder.append(str.verbFramesToCharSequence(verbFrame, lemma)).append('\n')
    }
}