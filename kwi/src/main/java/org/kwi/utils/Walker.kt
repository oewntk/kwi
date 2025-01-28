package org.kwi.utils

import org.kwi.IDictionary
import org.kwi.item.*
import org.kwi.item.Synset.Sense

/**
 * Tree exploration
 *
 * @author Bernard Bou
 */
open class Walker(val dict: IDictionary) {

    init {
        dict.open()
    }

    fun walk(lemma: String) {
        consumeLemma(lemma)
        POS.entries.forEach { pos ->
            walk(lemma, pos)
        }
    }

    fun walk(lemma: String, pos: POS) {
        val idx = dict.getIndex(lemma, pos)
        if (idx != null) {
            consumePos(pos)
            walkIndex(idx)
        }
    }

    fun walkIndex(idx: Index) {
        idx.pointers.forEach { ptr ->
            consumePointer(ptr)
        }
        idx.senseIDs.forEach { senseid ->
            consumeSenseID(senseid)
            walkSense(senseid)
            walkSynset(senseid.synsetID, 1)
        }
    }

    fun walkSense(senseid: SenseID) {
        val sense = dict.getSense(senseid)!!
        walkSense(sense)
    }

    fun walkSense(sense: Sense) {
        val senseEntry = dict.getSenseEntry(sense.senseKey)!!
        consumeSense(sense, senseEntry)

        // lexical relations
        walkRelatedSenses(sense.relatedSenses)

        // verb frames
        walkVerbFrames(sense.verbFrames, sense.lemma)
    }

    fun walkRelatedSenses(relatedSenses: Map<Pointer, List<SenseID>>) {
        relatedSenses.entries.forEach { (ptr, relatedSenseIDs) ->
            consumeRelatedSenseType(ptr, 1)
            walkSenseRelationsFor(relatedSenseIDs, ptr)
        }
    }

    fun walkSenseRelationsFor(relatedSenseIDs: List<SenseID>, ptr: Pointer) {
        relatedSenseIDs.forEach { relatedId ->
            val related = dict.getSense(relatedId)!!
            consumeRelatedSense(related, ptr, 2)
        }
    }

    // synset

    @JvmOverloads
    fun walkSynset(synsetID: SynsetID, level: Int = 0) {
        val synset = dict.getSynset(synsetID)!!
        walkSynset(synset, level)
    }

    @JvmOverloads
    fun walkSynset(synset: Synset, level: Int = 0) {
        consumeSynset(synset)
        synset.relatedSynsets.entries.forEach { (ptr, related) ->
            consumeRelatedSynsetType(ptr, level)
            walkSynsetRelationsFor(related, ptr, level)
        }
    }

    @JvmOverloads
    fun walkSynsetRelationsFor(synset: Synset, ptr: Pointer, level: Int = 0) {
        val relatedSynsetIDs = synset.getRelatedSynsetsFor(ptr)
        walkSynsetRelationsFor(relatedSynsetIDs, ptr, level)
    }

    @JvmOverloads
    fun walkSynsetRelationsFor(relatedSynsetIDs: List<SynsetID>, ptr: Pointer, level: Int = 0) {
        relatedSynsetIDs.forEach { relatedSynsetID ->
            val relatedSynset = dict.getSynset(relatedSynsetID)!!
            consumeRelatedSynset(relatedSynset, ptr, level)

            if (Pointer.canRecurse(ptr)) {
                walkSynsetRelationsFor(relatedSynset, ptr, level + 1)
            }
        }
    }

    fun walkVerbFrames(verbFrames: List<VerbFrame>?, lemma: String) {
        verbFrames?.forEach { verbFrame ->
            consumeVerbFrame(verbFrame, lemma)
        }
    }

    open fun consumeLemma(lemma: String) {}

    open fun consumePos(pos: POS) {}

    open fun consumePointer(ptr: Pointer) {}

    open fun consumeSenseID(senseid: SenseID) {}

    open fun consumeSynset(synset: Synset) {}

    open fun consumeSense(sense: Sense, senseEntry: SenseEntry? = null) {}

    open fun consumeVerbFrame(verbFrame: VerbFrame, lemma: String) {}

    @JvmOverloads
    open fun consumeRelatedSenseType(ptr: Pointer, level: Int = 0) {
    }

    @JvmOverloads
    open fun consumeRelatedSense(sense: Sense, pointer: Pointer, level: Int = 0) {
    }

    @JvmOverloads
    open fun consumeRelatedSynsetType(ptr: Pointer, level: Int = 0) {
    }

    @JvmOverloads
    open fun consumeRelatedSynset(synset: Synset, ptr: Pointer, level: Int = 0) {
    }
}