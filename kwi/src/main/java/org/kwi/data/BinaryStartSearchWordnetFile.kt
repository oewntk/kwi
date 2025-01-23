package org.kwi.data

import java.io.File
import java.nio.ByteBuffer

/**
 * A Wordnet file data source.
 * This particular implementation is for files on disk, and uses a binary search algorithm to find requested lines.
 * It is appropriate for alphabetically-ordered Wordnet files.
 *
 * Constructs a new Wordnet file binary search, on the specified file with the specified content type.
 *
 * @param file the file which backs this Wordnet file
 * @param contentType the content type for this file
 * @param <T> the type of object represented in this data resource
 */
class BinaryStartSearchWordnetFile<T>(file: File, contentType: ContentType<T>) : WordnetFile<T>(file, contentType) {

    private val comparator: Comparator<String>? = contentType.lineComparator

    private val bufferLock = Any()

    override fun getLine(key: String): String? {
        val buffer = getBuffer()

        synchronized(bufferLock) {
            var start = 0
            var stop = buffer.limit()
            while (stop - start > 1) {
                // find the middle of the buffer
                var midpoint: Int = (start + stop) / 2
                buffer.position(midpoint)

                // back up to the beginning of the line
                rewindToLineStart(buffer)

                // read line
                var line: String? = getLine(buffer, contentType.charset)

                // if we get a null, we've reached the end of the file
                var cmp: Int = if (line == null) 1 else comparator!!.compare(line, key)

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

    override fun makeIterator(buffer: ByteBuffer, key: String?): LineIterator {
        return BinarySearchLineIterator(buffer, key)
    }

    /**
     * Used to iterate over lines in a file.
     * It is a look-ahead iterator.
     *
     * Constructs a new line iterator over this buffer, starting at the specified key.
     *
     * @param buffer the buffer over which the iterator should iterator
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
                    getLine(itrBuffer, contentType.charset)
                    var offset: Int = itrBuffer.position()
                    var line = getLine(itrBuffer, contentType.charset)

                    if (line == null) {
                        // if the line is null, we've reached the end of the file, so just advance to the first line
                        itrBuffer.position(itrBuffer.limit())
                        return
                    }
                    var compare: Int = comparator!!.compare(line, key)
                    if (compare == 0) {
                        // if the key matches exactly, we know we have found the start of this pattern in the file
                        nextLine = line
                        return
                    } else if (compare > 0) {
                        stop = midpoint
                    } else {
                        start = midpoint
                        // remember last position before
                        lastOffset = offset
                    }
                }

                // getting here means that we didn't find an exact match to the key, so we take the last line that started with the pattern
                if (lastOffset > -1) {
                    itrBuffer.position(lastOffset)
                    nextLine = getLine(itrBuffer, contentType.charset)
                    return
                }

                // if we didn't have any lines that matched the pattern then just advance to the first non-comment
                itrBuffer.position(itrBuffer.limit())
            }
        }
    }
}
