/*
 * Copyright (c) 2025.
 * Java Wordnet Interface Library (JWI) v2.4.0 Copyright (c) 2007-2015 Mark A. Finlayson
 * Kotlin Wordnet Interface Library (KWI) v1.0 Copyright (c) 2025 Bernard Bou
 * JWI is distributed under the terms of the Creative Commons Attribution 4.0 International Public License
 * KWI is distributed under the terms of the GPL3 License
 */

package org.kwi.data

import java.io.File
import java.nio.ByteBuffer
import java.nio.charset.Charset

/**
 * A Wordnet file data source with binary search.
 * This particular implementation is for files on disk, and uses a binary search algorithm to find requested lines.
 * It is appropriate for alphabetically-ordered Wordnet files.
 *
 * Constructs a new Wordnet file binary search, on the specified file with the specified content type.
 *
 * @param file the file which backs this Wordnet file
 * @param contentType the content type for this file
 * @param charset the character sset used for decoding
 * @param <T> the type of object represented in this data resource
 */
class BinarySearchWordnetFile<T>(
    file: File,
    contentType: ContentType<T>,
    charset: Charset?,
) : WordnetFile<T>(file, contentType, charset) {

    private val comparator: Comparator<String>? = contentType.lineComparator

    private val bufferLock = Any()

    override fun getLine(key: String): String? {
        val buffer = getBuffer()

        synchronized(bufferLock) {
            var start = 0
            var stop = buffer.limit()
            while (stop - start > 1) {

                // find the middle of the buffer
                val midpoint: Int = (start + stop) / 2
                buffer.position(midpoint)

                // back up to the beginning of the line
                rewindToLineStart(buffer)

                // read line
                var line: String? = getLine(buffer, charset)

                // if we get a null, we've reached the end of the file
                val cmp: Int = if (line == null) 1 else comparator!!.compare(line, key)

                // found our line
                if (cmp == 0) {
                    return line
                }
                if (cmp > 0) {
                    // too far forward
                    stop = midpoint
                } else {
                    // too far back
                    start = midpoint
                }
            }
        }
        return null
    }

    /**
     * Constructs a new line iterator over this buffer, starting at the specified key.
     *
     * @param buffer the buffer over which the iterator should iterate
     * @param key the key of the line to start at
     */
    override fun makeIterator(buffer: ByteBuffer, key: String?): LineIterator {
        return BinarySearchLineIterator(buffer, key)
    }

    /**
     * Iterator over lines in a file.
     * It is a look-ahead iterator.
     *
     * Constructs a new line iterator over this buffer, starting at the specified key.
     *
     * @param buffer the buffer over which the iterator should iterate
     * @param key the key of the line to start at
     */
    inner class BinarySearchLineIterator(buffer: ByteBuffer, key: String?) : LineIterator(buffer) {

        private val bufferLock: Any = Any()

        init {
            startAt(key)
        }

        override fun findFirstLine(key: String) {
            synchronized(bufferLock) {
                var lastOffset = -1
                var start = 0
                var stop = itrBuffer.limit()
                while (start + 1 < stop) {
                    var midpoint: Int = (start + stop) / 2

                    itrBuffer.position(midpoint)
                    getLine(itrBuffer, charset)
                    var offset: Int = itrBuffer.position()
                    var line = getLine(itrBuffer, charset)

                    if (line == null) {
                        // if the line is null, we've reached the end of the file, so just advance to the first line
                        itrBuffer.position(itrBuffer.limit())
                        return
                    }

                    var compare: Int = comparator!!.compare(line, key)
                    if (compare == 0) {
                        nextLine = line
                        return
                    } else if (compare > 0) {
                        stop = midpoint
                    } else {
                        start = midpoint
                    }
                    // if the key starts a line, remember it, because it may be the first occurrence
                    if (line.startsWith(key)) {
                        lastOffset = offset
                    }
                }

                // getting here means that we didn't find an exact match to the key, so we take the last line that started with the pattern
                if (lastOffset > -1) {
                    itrBuffer.position(lastOffset)
                    nextLine = getLine(itrBuffer, charset)
                    return
                }

                // if we didn't have any lines that matched the pattern then just advance to the first non-comment
                itrBuffer.position(itrBuffer.limit())
            }
        }
    }
}
