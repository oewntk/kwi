package org.kwi.utils

import org.kwi.IDictionary
import org.kwi.item.Index
import org.kwi.item.POS
import org.kwi.item.SenseID
import org.kwi.item.SynsetID
import java.io.PrintStream

/**
 * Tree exploration
 *
 * @author Bernard Bou
 */
open class PrintWalker(
    dict: IDictionary,
    str: Stringifier,
    val ps: PrintStream,

    ) : BuilderWalker(dict, str, StringBuilder()) {

    override fun walkTop(lemma: String) {
        walk(lemma)
        ps.print(builder.toString())
    }

    override fun walkTop(lemma: String, pos: POS) {
        walk(lemma, pos)
        ps.print(builder.toString())
    }

    override fun walkTop(idx: Index) {
        walkIndex(idx)
        ps.print(builder.toString())
    }

    override fun walkTop(senseid: SenseID) {
        walkSense(senseid)
        ps.print(builder.toString())
    }

    override fun walkTop(synsetid: SynsetID) {
        walkSynset(synsetid)
        ps.print(builder.toString())
    }
}