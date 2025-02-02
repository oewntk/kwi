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
 * @param dict dictionary
 * @param str stringifier
 * @param ps print stream to output builder to
 * @param maxLevel maximum recursion level
 *
 * @author Bernard Bou
 */
open class PrintWalker(
    dict: IDictionary,
    str: Stringifier,
    val ps: PrintStream,
    maxLevel: Int = 0,

    ) : BuilderWalker(dict, str, StringBuilder(), maxLevel) {

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