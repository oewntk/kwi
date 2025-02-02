/*
 * Copyright (c) 2025.
 * Java Wordnet Interface Library (JWI) v2.4.0 Copyright (c) 2007-2015 Mark A. Finlayson
 * Kotlin Wordnet Interface Library (KWI) v1.0 Copyright (c) 2025 Bernard Bou
 * JWI is distributed under the terms of the Creative Commons Attribution 4.0 International Public License
 * KWI is distributed under the terms of the GPL3 License
 */

package org.kwi.data.parse

import org.kwi.item.ExceptionProtoEntry

/**
 * Parser for Wordnet exception files (e.g., `exc.adv` or `adv.exc`).
 * This parser produces ExceptionEntryProxy objects instead of ExceptionEntry objects directly because the exception files do not contain information about part-of-speech.
 * This needs to be added by the governing object to create a full-fledged ExceptionEntry object.
 */
object ExceptionLineParser : ILineParser<ExceptionProtoEntry> {

    override fun parseLine(line: String): ExceptionProtoEntry {

        val forms = SEPARATOR.split(line).asSequence().map { it.trim { it <= ' ' } }.toList()
        if (forms.size < 2) {
            throw MisformattedLineException(line)
        }
        val surface = forms[0]
        val roots = forms.slice(2 until forms.size)
        return ExceptionProtoEntry(surface, roots)
    }

    private val SEPARATOR = " ".toRegex()
}
