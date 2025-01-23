package org.kwi.data.parse

import org.kwi.item.LexFile.Companion.getLexicalFile
import org.kwi.item.POS.Companion.getPartOfSpeech
import org.kwi.item.Pointer.Companion.getPointerType
import org.kwi.item.Synset.Companion.normalizeRelatedSense
import org.kwi.item.Synset.Companion.normalizeRelatedSynset
import org.kwi.item.Synset.Member
import org.kwi.item.VerbFrame.Companion.getFrame
import org.kwi.item.AdjMarker
import org.kwi.item.LexFile
import org.kwi.item.POS
import org.kwi.item.Pointer
import org.kwi.item.SenseIDWithNum
import org.kwi.item.Synset
import org.kwi.item.SynsetID
import org.kwi.item.VerbFrame
import java.util.*

data class RelationData(val pointer: Pointer, val targetSynsetID: SynsetID, var sourceTargetNum: Int)

/**
 * Parser for Wordnet data files (e.g., `data.adv` or `adv.dat`).
 * This parser produces a Synset object.
 */
object DataLineParser : ILineParser<Synset> {

    override fun parseLine(line: String): Synset {

        try {
            val tokenizer = StringTokenizer(line, " ")

            // offset
            val offset = tokenizer.nextToken().toInt()

            // lex_filenum
            val lexFilenum = tokenizer.nextToken().toInt()
            val lexFile = resolveLexicalFile(lexFilenum)

            // part-of-speech
            val synsetTag = tokenizer.nextToken()[0]
            val synsetPos = getPartOfSpeech(synsetTag)

            // ID
            val synsetID = SynsetID(offset, synsetPos)

            // adjective satellite
            val isAdjSat = (synsetTag == 's')

            // adjective head
            // A synset is an adjective head if it is the 00 lexical file, is not an adjective satellite, and it has an antonym.
            // The Wordnet definition says head synsets have to have an antonym, but this is actually violated (perhaps mistakenly) in a small number of cases,
            // e.g., in Wordnet 3.0:
            // 01380267 aerial (no antonyms), with satellite 01380571 free-flying
            // 01380721 marine (no antonyms), with satellite 01380926 deep-sea
            val isAdjHead = !isAdjSat && lexFilenum == 0

            // sense count
            val senseCount = tokenizer.nextToken().toInt(16)

            // members
            val members = Array<Member>(senseCount) {

                // member lemma
                var lemma = tokenizer.nextToken()

                // if it is an adjective, it may be followed by a marker
                val marker: AdjMarker? = if (synsetPos != POS.ADJECTIVE) null else AdjMarker.entries.firstOrNull { lemma.endsWith(it.symbol) }
                marker?.let {
                    lemma = lemma.substring(0, lemma.length - it.symbol.length)
                }

                // lex_id
                val lexicalID = tokenizer.nextToken().toInt(16)

                Member(it + 1, lemma, lexicalID, marker, emptyMap(), emptyList())
            }

            // pointers
            val pointerCount = tokenizer.nextToken().toInt()
            val relations = List(pointerCount) {

                // get pointer symbol
                val symbol = tokenizer.nextToken()

                // get synset target offset
                val targetOffset = tokenizer.nextToken().toInt()

                // get target synset part-of-speech
                val targetPos = getPartOfSpeech(tokenizer.nextToken()[0])

                // get source/target numbers
                var sourceTargetNum = tokenizer.nextToken().toInt(16)

                // get pointer
                val pointer = resolvePointer(symbol, synsetPos)

                // ID
                var targetSynsetID = SynsetID(targetOffset, targetPos)

                RelationData(pointer, targetSynsetID, sourceTargetNum)

            }.partition { it.sourceTargetNum == 0 }

            val synsetRelations = relations.first
                .groupBy { data -> data.pointer }
                .mapValues { (_, data) -> data.map { it.targetSynsetID } }
            normalizeRelatedSynset(synsetRelations)

            val senseRelations = relations.second
                .groupBy { it.sourceTargetNum / 256 }
                .mapValues { (_, data) ->
                    data
                        .groupBy { it.pointer }
                        .mapValues { (_, data) ->
                            data.map {
                                val targetNum: Int = it.sourceTargetNum and 255
                                val senseid = SenseIDWithNum(it.targetSynsetID, targetNum)
                                senseid
                            }
                        }
                }
            // transfer sense relations members
            senseRelations.entries.forEach {
                members[it.key - 1].related = normalizeRelatedSense(it.value)
            }

            // parse verb frames
            // do not make the field compulsory for verbs with a 00 when no frame is present
            if (synsetPos == POS.VERB) {
                val peekTok = tokenizer.nextToken()
                if (!peekTok.startsWith("|")) {
                    val verbFrameCount = peekTok.toInt()

                    val frames = List(verbFrameCount) {
                        // Consume '+'
                        tokenizer.nextToken()

                        // Get frame number
                        var frameNum: Int = tokenizer.nextToken().toInt()
                        var frame: VerbFrame = resolveVerbFrame(frameNum)

                        // Get sense number
                        val senseNum: Int = tokenizer.nextToken().toInt(16)
                        senseNum to frame

                    }.partition { it.first > 0 }
                    val allSensesFrames = frames.first
                        .map { (_, frame) -> frame }
                    val senseFrames = frames.second
                        .groupBy { (index, _) -> index }
                        .mapValues { (_, value) -> value.map { (_, frame) -> frame } }

                    // transfer to sense builders
                    senseFrames.entries.forEach {
                        members[it.key].verbFrames = it.value + allSensesFrames
                    }
                }
            }

            // gloss
            val cut = line.indexOf('|')
            val gloss = if (cut > 0) line.substring(cut + 2).trim { it <= ' ' } else ""

            // create synset
            return Synset(synsetID, members, lexFile, gloss, synsetRelations, isAdjSat, isAdjHead, null)

        } catch (e: NumberFormatException) {
            throw MisformattedLineException(line, e)
        } catch (e: NoSuchElementException) {
            throw MisformattedLineException(line, e)
        }
    }

    /**
     * Retrieves the verb frames for the parseLine method.
     * This is implemented in its own method for ease of subclassing.
     * @param frameNum the number of the frame to return
     * @return the verb frame corresponding to the specified frame number, or null if there is none
     */
    private fun resolveVerbFrame(frameNum: Int): VerbFrame {
        return getFrame(frameNum)!!
    }

    /**
     * Retrieves the lexical file objects for the parseLine method.
     * If the lexical file number does correspond to a known lexical file, the method returns a singleton placeholder 'unknown' lexical file object.
     * This is implemented in its own method for ease of subclassing.
     * @param lexFileNum the number of the lexical file to return
     * @return the lexical file corresponding to the specified frame number
     */
    private fun resolveLexicalFile(lexFileNum: Int): LexFile {
        return getLexicalFile(lexFileNum)
    }

    /**
     * Retrieves the pointer objects for the parseLine method.
     * This is implemented in its own method for ease of subclassing.
     *
     * @param symbol the symbol of the pointer to return
     * @param pos the part-of-speech of the pointer to return, can be null unless the pointer symbol is ambiguous
     * @return the pointer corresponding to the specified symbol and part-of-speech combination
     * @throws IllegalArgumentException if the symbol and part-of-speech combination does not correspond to a known pointer
     */
    private fun resolvePointer(symbol: String, pos: POS?): Pointer {
        return getPointerType(symbol, pos)
    }
}
