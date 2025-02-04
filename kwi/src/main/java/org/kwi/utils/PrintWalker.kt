/*
 * Copyright (c) 2025.
 * Java Wordnet Interface Library (JWI) v2.4.0 Copyright (c) 2007-2015 Mark A. Finlayson
 * Kotlin Wordnet Interface Library (KWI) v1.0 Copyright (c) 2025 Bernard Bou
 * JWI is distributed under the terms of the Creative Commons Attribution 4.0 International Public License
 * KWI is distributed under the terms of the GPL3 License
 */

package org.kwi.utils

import org.kwi.IDictionary
import org.kwi.item.Index
import org.kwi.item.POS
import org.kwi.item.SenseID
import org.kwi.item.SynsetID
import java.io.PrintStream

/**
 * Tree exploration that prints to a print stream
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