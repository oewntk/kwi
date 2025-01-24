package org.kwi.item

import org.kwi.data.ContentType
import org.kwi.data.WordnetFile.Companion.getLine
import java.io.Serializable
import java.nio.ByteBuffer

/**
 * Version
 *
 * Creates a new version object with the specified version numbers.
 *
 * @throws IllegalArgumentException if any of the version numbers are negative, or the qualifier is not a legal qualifier
 */
class Version(val version: String) : Serializable {

    override fun toString(): String {
        return version
    }

    companion object {

        // fields for version parsing
         private val versionPattern = "Word[Nn]et (.*) Copyright".toRegex()

        /**
         * Extracts a version object from a byte buffer that contains data with the specified content type.
         * If no version can be extracted, returns null.
         *
         * @param contentType the content type of the data in the buffer
         * @param buffer the buffer containing the data
         * @return the Version that was extracted, or null if none
         */
        fun extractVersion(contentType: ContentType<*>, buffer: ByteBuffer): Version? {
            val dataType = contentType.dataType
            if (!dataType.hasVersion) {
                return null
            }

            // try walking forward in file until we find a string that looks like "Word[Nn]et xxx Copyright"
            val cd = contentType.lineComparator?.commentDetector
            if (cd == null) {
                return null
            }

            val origPos = buffer.position()
            try {
                buffer.position(0)
                while (buffer.position() < buffer.limit()) {
                    var line: String? = getLine(buffer, Charsets.UTF_8)
                    if (line == null || !cd.isCommentLine(line)) {
                        break
                    }
                    var r = versionPattern.find(line)
                    if (r != null) {
                        var group = r.groupValues[1]
                        return parseVersion(group)
                    }
                }
                return null
            } finally {
                buffer.position(origPos)
            }
        }

        /**
         * Tries to transform the specified string into a version object.
         * If it cannot, returns null
         *
         * @param str the sequence of characters to be transformed
         * @return the version, or null if the character sequence is not a valid version
         */
        fun parseVersion(str: String?): Version? {
            if (str == null) {
                return null
            }
            return Version(str)
        }
    }
}
