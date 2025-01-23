package org.kwi.utils

import org.kwi.IDictionary
import org.kwi.item.Index
import org.kwi.item.POS
import org.kwi.item.Pointer
import org.kwi.item.SenseEntry
import org.kwi.item.SenseID
import org.kwi.item.Synset
import org.kwi.item.Synset.Sense
import org.kwi.item.SynsetID
import org.kwi.item.VerbFrame

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
            walk(idx)
        }
    }

    fun walk(idx: Index) {
        idx.pointers.forEach { ptr ->
            consumePointer(ptr)
        }
        idx.senseIDs.forEach { senseid ->
            consumeSenseID(senseid)
            walk(senseid)
        }
    }

    fun walk(senseid: SenseID) {
        val sense = dict.getSense(senseid)!!
        val synset = dict.getSynset(senseid.synsetID)!!

        walk(synset, 1)
        walk(sense)
    }

    fun walk(sense: Sense) {
        val senseEntry = dict.getSenseEntry(sense.senseKey)!!
        consumeSense(sense, senseEntry)

        // lexical relations
        walk(sense.relatedSenses)

        // verb frames
        walk(sense.verbFrames, sense.lemma)
    }

    fun walk(relatedSenses: Map<Pointer, List<SenseID>>) {
        relatedSenses.entries.forEach { (ptr, related) ->
            consumeRelatedSenseType(ptr, 1)
            walk(related, ptr)
        }
    }

    fun walk(relatedSenseIDs: List<SenseID>, ptr: Pointer) {
        relatedSenseIDs.forEach { relatedId ->
            val related = dict.getSense(relatedId)!!
            consumeRelatedSense(related, ptr, 2)
        }
    }

    fun walk(verbFrames: List<VerbFrame>?, lemma: String) {
        verbFrames?.forEach { verbFrame ->
            consumeVerbFrame(verbFrame, lemma)
        }
    }

    fun walk(synset: Synset, level: Int) {
        consumeSynset(synset)
        synset.relatedSynsets.entries.forEach { (ptr, related) ->
            consumeRelatedSynsetType(ptr, level)
            walk(related, ptr, level)
        }
    }

    fun walk(relatedSynsetIDs: List<SynsetID>, ptr: Pointer, level: Int) {
        relatedSynsetIDs.forEach { relatedID ->
            val synset = dict.getSynset(relatedID)!!
            consumeRelatedSynset(synset, ptr, level)
            walk(synset, ptr, level + 1)
        }
    }

    fun walk(synset: Synset, ptr: Pointer, level: Int) {
        val relatedIDs = synset.getRelatedSynsetsFor(ptr)
        relatedIDs.forEach { synsetid2 ->
            val synset2 = dict.getSynset(synsetid2)!!
            consumeRelatedSynset(synset2, ptr, level)
            if (Pointer.canRecurse(ptr)) {
                walk(synset2, ptr, level + 1)
            }
        }
    }

    open fun consumeLemma(lemma: String) {}

    open fun consumePos(pos: POS) {}

    open fun consumePointer(ptr: Pointer) {}

    open fun consumeSenseID(senseid: SenseID) {}

    open fun consumeSynset(synset: Synset) {}

    open fun consumeSense(sense: Sense, senseEntry: SenseEntry? = null) {}

    open fun consumeVerbFrame(verbFrame: VerbFrame, lemma: String) {}

    open fun consumeRelatedSenseType(ptr: Pointer, level: Int) {}

    open fun consumeRelatedSense(sense: Sense, pointer: Pointer, level: Int) {}

    open fun consumeRelatedSynsetType(ptr: Pointer, level: Int) {}

    open fun consumeRelatedSynset(synset: Synset, ptr: Pointer, level: Int) {}
}