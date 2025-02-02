/*
 * Copyright (c) 2025.
 * Java Wordnet Interface Library (JWI) v2.4.0 Copyright (c) 2007-2015 Mark A. Finlayson
 * Kotlin Wordnet Interface Library (KWI) v1.0 Copyright (c) 2025 Bernard Bou
 * JWI is distributed under the terms of the Creative Commons Attribution 4.0 International Public License
 * KWI is distributed under the terms of the GPL3 License
 */

package org.kwi.utils

import org.kwi.IDictionary
import org.kwi.item.*

object Sequences {

    fun IDictionary.seqAllIndexes(): Sequence<Index> = POS.entries.flatMap { pos -> this@seqAllIndexes.getIndexIterator(pos).asSequence() }.asSequence()

    fun IDictionary.seqAllSynsets(): Sequence<Synset> = POS.entries.flatMap { pos -> this@seqAllSynsets.getSynsetIterator(pos).asSequence() }.asSequence()

    fun IDictionary.seqAllLemmas(): Sequence<String> = seqAllIndexes().map { it.lemma }

    fun IDictionary.seqAllSenses(): Sequence<Synset.Sense> = seqAllIndexes().flatMap { it.senseIDs.map { this@seqAllSenses.getSense(it)!! } }

    fun IDictionary.seqAllSenseKeys(): Sequence<SenseKey> = seqAllSenses().map { it.senseKey }

    fun IDictionary.seqAllSenseEntries(): Sequence<SenseEntry> = this@seqAllSenseEntries.getSenseEntryIterator().asSequence()

    fun IDictionary.seqAllExceptionEntries(): Sequence<ExceptionEntry> = POS.entries.flatMap { pos -> this@seqAllExceptionEntries.getExceptionEntryIterator(pos).asSequence() }.asSequence()

    fun IDictionary.seqAllSynsetRelations(): Sequence<Relation<Synset>> = sequence {
        seqAllSynsets().forEach { synset ->
            synset.relatedSynsets.keys.forEach { ptr ->
                synset.relatedSynsets[ptr]!!.forEach {
                    val related = this@seqAllSynsetRelations.getSynset(it)!!
                    yield(Relation(ptr.toString(), synset to related))
                }
            }
        }
    }

    fun IDictionary.seqAllFlatSynsetRelations(): Sequence<Pair<Synset, Synset>> = sequence {
        seqAllSynsets().forEach { synset ->
            synset.allRelated.forEach {
                val related = this@seqAllFlatSynsetRelations.getSynset(it)!!
                yield(synset to related)
            }
        }
    }

    fun IDictionary.seqAllSenseRelations(): Sequence<Relation<Synset.Sense>> = sequence {
        seqAllSenses().forEach { sense ->
            sense.relatedSenses.keys.forEach { ptr ->
                sense.relatedSenses[ptr]!!.forEach {
                    val related = this@seqAllSenseRelations.getSense(it)!!
                    yield(Relation(ptr.toString(), sense to related))
                }
            }
        }
    }

    fun IDictionary.seqAllFlatSenseRelations(): Sequence<Pair<Synset.Sense, Synset.Sense>> = sequence {
        seqAllSenses().forEach { sense ->
            sense.relatedSenses.keys.forEach { ptr ->
                sense.allRelatedSenses.forEach {
                    val related = this@seqAllFlatSenseRelations.getSense(it)!!
                    yield(sense to related)
                }
            }
        }
    }

    // F R O M   W O R D

    fun IDictionary.seqAllSenseIDs(lemma: String, pos: POS): Sequence<SenseID> = this@seqAllSenseIDs.getIndex(lemma, pos)!!.senseIDs.asSequence()

    fun IDictionary.seqAllSenseIDs(lemma: String): Sequence<SenseID> = POS.entries.flatMap { pos -> seqAllSenseIDs(lemma, pos) }.asSequence()

    fun IDictionary.seqAllSenses(lemma: String, pos: POS): Sequence<Synset.Sense> = seqAllSenseIDs(lemma, pos).map { this@seqAllSenses.getSense(it)!! }

    fun IDictionary.seqAllSenses(lemma: String): Sequence<Synset.Sense> = POS.entries.flatMap { pos -> seqAllSenses(lemma, pos) }.asSequence()

    fun seqMembers(synset: Synset): Sequence<String> = synset.senses.asSequence().map { it.lemma }
}