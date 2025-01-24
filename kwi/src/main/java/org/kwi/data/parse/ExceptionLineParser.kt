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
