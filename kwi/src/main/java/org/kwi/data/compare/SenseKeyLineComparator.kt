/*
 * Copyright (c) 2025.
 * Java Wordnet Interface Library (JWI) v2.4.0 Copyright (c) 2007-2015 Mark A. Finlayson
 * Kotlin Wordnet Interface Library (KWI) v1.0 Copyright (c) 2025 Bernard Bou
 * JWI is distributed under the terms of the Creative Commons Attribution 4.0 International Public License
 * KWI is distributed under the terms of the GPL3 License
 */

package org.kwi.data.compare

/**
 * A comparator that captures the ordering of lines in sense index files (e.g., the `sense.index` file).
 * These files are ordered alphabetically by sense key.
 */
object SenseKeyLineComparator : ILineComparator {

    override var commentDetector: ICommentDetector? = null

    override fun compare(line1: String, line2: String): Int {
        // get sense keys
        val cut1 = line1.indexOf(' ')
        val sk1 = if (cut1 == -1) line1 else line1.substring(0, cut1)

        val cut2 = line2.indexOf(' ')
        val sk2 = if (cut2 == -1) line2 else line2.substring(0, cut2)

        return compareAsSenseKeys(sk1, sk2)
    }

    /**
     * Compare sense keys as SenseKeys
     *
     * @param senseKey1 sense key 1
     * @param senseKey2 sense key 1
     * @return compare code
     */
    private fun compareAsSenseKeys(senseKey1: String, senseKey2: String): Int {
        return senseKey1.compareTo(senseKey2, ignoreCase = true)
    }
}
