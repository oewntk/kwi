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
 * A Wordnet file data source.
 * This particular implementation is for files on disk, and directly accesses the appropriate byte offset in the file to find requested lines.
 * It is appropriate for Wordnet data files.
 *
 * Constructs a new direct access Wordnet file, on the specified file with the specified content type.
 *
 * @param file the file which backs this Wordnet file
 * @param contentType the content type for this file
 * @param charset the character sset used for decoding
 * @param <T> the type of object represented in this data resource
 */
class DirectAccessWordnetFile<T>(
    file: File,
    contentType: ContentType<T>,
    charset: Charset?,
) : WordnetFile<T>(file, contentType, charset) {

    private val bufferLock = Any()

    override fun getLine(key: String): String? {

        val buffer = obtainBuffer()
        synchronized(bufferLock) {
            try {
                val byteOffset = key.toInt()
                if (buffer.limit() <= byteOffset) {
                    return null
                }
                buffer.position(byteOffset)
                val line = getLine(buffer, charset)
                return if (line != null && line.startsWith(key)) line else null
            } catch (_: NumberFormatException) {
                return null
            }
        }
    }

    override fun makeIterator(buffer: ByteBuffer, key: String?): LineIterator {
        return DirectLineIterator(buffer, key)
    }

    /**
     * Used to iterate over lines in a file. It is a look-ahead iterator.
     *
     * Constructs a new line iterator over this buffer, starting at the specified key.
     *
     * @param buffer the buffer over which the iterator should iterator
     * @param key the key of the line to start at
     */
    private inner class DirectLineIterator(buffer: ByteBuffer, key: String?) : LineIterator(buffer) {

        private val bufferLock = Any()

        init {
            startAt(key)
        }

        override fun findFirstLine(key: String) {

            synchronized(bufferLock) {
                try {
                    val byteOffset = key.toInt()
                    if (itrBuffer.limit() <= byteOffset) {
                        return
                    }
                    itrBuffer.position(byteOffset)
                    nextLine = getLine(itrBuffer, charset)
                } catch (_: NumberFormatException) {
                    // ignore
                }
            }
        }
    }
}
