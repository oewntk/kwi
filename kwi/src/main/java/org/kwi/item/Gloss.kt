/*
 * Copyright (c) 2021-2025.
 * Java Wordnet Interface Library (JWI) v2.4.0 Copyright (c) 2007-2015 Mark A. Finlayson
 * Kotlin Wordnet Interface Library (KWI) v1.0 Copyright (c) 2025 Bernard Bou
 * JWI is distributed under the terms of the Creative Commons Attribution 4.0 International Public License
 * KWI is distributed under the terms of the GPL3 License
 */
package org.kwi.item

/**
 * Gloss
 *
 * @param gloss raw gloss
 * @property splitGloss split gloss
 *
 * @author Bernard Bou
 */
class Gloss(
    gloss: String,
) {
    private val splitGloss: Array<String> = split(gloss.trim { it <= ' ' })

    /**
     * Parse gloss into fields
     *
     * @param gloss gloss
     * @return fields
     */
    private fun split(gloss: String): Array<String> {

        val quoteCount = countQuotes(gloss)
        if (quoteCount % 2 != 0) {
            System.err.println("Uneven quotes $quoteCount in :$gloss")
        }

        val pattern = Regex("\"[^\"]*\"").toPattern()
        val matcher = pattern.matcher(gloss) // get a matcher object
        var count = 0
        var split = -1
        while (matcher.find()) {
            if (count == 0) {
                split = matcher.start()
            }
            count++
        }

        var definition = if (split == -1) gloss else gloss.substring(0, split)
        definition = definition.replaceFirst("[;\\s]*$".toRegex(), "")
        matcher.reset()

        val result = Array(count + 1) {
            if (it == 0)
                // [0] definition
                definition
            else {
                // [1-n] samples
                matcher.find()
                var sample = matcher.group()
                if (sample.startsWith("\"") && sample.endsWith("\"")) {
                    sample = sample.substring(1, sample.length - 1)
                }
                sample
            }
        }
        return result
    }

    private fun countQuotes(str: String): Int {
        var quoteCount = 0
        var p = -1
        while ((str.indexOf('"', p + 1).also { p = it }) != -1) {
            quoteCount++
        }
        return quoteCount
    }

    /**
     * Get definition
     *
     * @return definition
     */
    val definition: String
        get() = splitGloss[0]

    /**
     * Get samples
     *
     * @return samples
     */
    val examples: Array<String>
        get() = this.splitGloss.copyOfRange(1, splitGloss.size)

    /**
     * Get string
     *
     * @return string
     */
    override fun toString(): String = splitGloss.joinToString(separator = ";")
}
