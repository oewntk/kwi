package org.kwi.utils

import org.kwi.IDictionary
import org.kwi.item.*
import org.kwi.item.Synset.Sense

/**
 * Tree exploration
 *
 * @property dict dictionary
 * @property maxLevel maximum recursion level
 *
 * @author Bernard Bou
 */
open class Walker(
    val dict: IDictionary,
    val maxLevel: Int = 0,

    ) {

    init {
        dict.open()
    }

    open fun walkTop(lemma: String) {
        walk(lemma)
    }

    open fun walkTop(lemma: String, pos: POS) {
        walk(lemma, pos)
    }

    open fun walkTop(idx: Index) {
        walkIndex(idx)
    }

    open fun walkTop(senseid: SenseID) {
        walkSense(senseid)
    }

    open fun walkTop(synsetid: SynsetID) {
        walkSynset(synsetid)
    }

    protected fun walk(lemma: String) {
        consumeLemma(lemma)
        POS.entries.forEach { pos ->
            walk(lemma, pos)
        }
    }

    protected fun walk(lemma: String, pos: POS) {
        val idx = dict.getIndex(lemma, pos)
        if (idx != null) {
            consumePos(pos)
            walkIndex(idx)
        }
    }

    protected fun walkIndex(idx: Index) {
        idx.pointers.forEach { ptr ->
            consumePointer(ptr)
        }
        idx.senseIDs.forEach { senseid ->
            consumeSenseID(senseid)
            walkSense(senseid)
            walkSynset(senseid.synsetID, 1)
        }
    }

    protected fun walkSense(senseid: SenseID) {
        val sense = dict.getSense(senseid)!!
        walkSense(sense)
    }

    protected fun walkSense(sense: Sense) {
        val senseEntry = dict.getSenseEntry(sense.senseKey)!!
        consumeSense(sense, senseEntry)

        // lexical relations
        walkRelatedSenses(sense.relatedSenses)

        // verb frames
        walkVerbFrames(sense.verbFrames, sense.lemma)
    }

    protected fun walkRelatedSenses(relatedSenses: Map<Pointer, List<SenseID>>) {
        relatedSenses.entries.forEach { (ptr, relatedSenseIDs) ->
            consumeRelatedSenseType(ptr, 1)
            walkSenseRelationsFor(relatedSenseIDs, ptr, 2)
        }
    }

    protected fun walkSenseRelationsFor(relatedSenseIDs: List<SenseID>, ptr: Pointer, level: Int = 0) {
        relatedSenseIDs.forEach { relatedId ->
            val related = dict.getSense(relatedId)!!
            consumeRelatedSense(related, ptr, level + 1)
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
            walkSynsetRelationsFor(related, ptr, level + 1)
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

            if (Pointer.canRecurse(ptr) && (level > 0 || level < maxLevel)) {
                walkSynsetRelationsFor(relatedSynset, ptr, level + 1)
            }
        }
    }

    // verb frames

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