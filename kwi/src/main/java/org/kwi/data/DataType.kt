/*
 * Copyright (c) 2025.
 * Java Wordnet Interface Library (JWI) v2.4.0 Copyright (c) 2007-2015 Mark A. Finlayson
 * Kotlin Wordnet Interface Library (KWI) v1.0 Copyright (c) 2025 Bernard Bou
 * JWI is distributed under the terms of the Creative Commons Attribution 4.0 International Public License
 * KWI is distributed under the terms of the GPL3 License
 */

package org.kwi.data

import org.kwi.data.parse.*
import org.kwi.item.*
import java.io.File

/**
 * Objects that represent possible types of data that occur in the dictionary data directory.
 *
 * In the standard Wordnet distributions, data types would include, but would not be limited to,
 * *Index* files,
 * *Data* files, and
 * *Exception* files.
 * The objects implementing this interface are then paired with a POS instance and ILineComparator instance to form an instance of an IContentType class, which identifies the specific data contained in the file.
 * Note that here, 'data' refers not to an actual file, but to an instance of the IDataSource interface that provides access to the data, be it a file in the file system, a socket connection to a database, or something else.
 *
 * This class provides the data types necessary for Wordnet in the form of properties. It is not implemented as an `Enum` so that clients may add their own content types by instantiating this class.
 *
 * Constructs a new data type. This constructor takes the hints as a varargs array.
 *
 * @param name a user-friendly name, for easy identification of this data type
 * @param hasVersion true if the comment header for this data type usually contains a version number
 * @param parser the line parser for transforming lines from this data type into objects
 * @param resourceNameHints a varargs array of resource name hints for identifying the resource that contains the data
 * @param <T> the type of object for the content type, the type of the object returned by the parser for this data type
 */
class DataType<T>(
    private val name: String,
    internal val hasVersion: Boolean,
    val parser: ILineParser<T>,
    val resourceNameHints: Array<String>,
) {

    override fun toString(): String {
        return name
    }

    /**
     * Finds the first file that satisfies the naming constraints of both the data type and part-of-speech.
     *
     * @param pos the part-of-speech whose resource name hints should be used
     * @param files the files to be searched
     * @return the file that matches both the pos and type naming conventions, or null if none is found.
     */
    fun find(pos: POS?, files: Collection<File>): File? {
        val posPatterns: Set<String> = pos?.resourceNameHints ?: emptySet()
        for (typePattern in resourceNameHints) {
            for (file in files) {
                val name = file.name.lowercase()
                if (name.contains(typePattern) && containsOneOf(name, posPatterns)) {
                    return file
                }
            }
        }
        return null
    }

    companion object {

        @JvmField
        val INDEX: DataType<Index> = DataType<Index>("Index", true, IndexLineParser, arrayOf("index", "idx"))

        @JvmField
        val DATA: DataType<Synset> = DataType<Synset>("Data", true, DataLineParser, arrayOf("data", "dat"))

        @JvmField
        val EXCEPTION: DataType<ExceptionProtoEntry> = DataType<ExceptionProtoEntry>("Exception", false, ExceptionLineParser, arrayOf("exception", "exc"))

        @JvmField
        val SENSE: DataType<SenseEntry> = DataType<SenseEntry>("Sense", false, SenseLineParser, arrayOf("sense"))

        /**
         * Set of all data types implemented in this class
         */
        private val dataTypes: Set<DataType<*>> = setOf(INDEX, DATA, EXCEPTION, SENSE)

        /**
         * Emulates the Enum.values() function.
         */
        val values: Collection<DataType<*>>
            get() = dataTypes

        /**
         * Checks to see if one of the string patterns specified in the set of strings is found in the specified target string.
         * If the pattern set is empty, returns true.
         * If a pattern is found in the target string, returns true; otherwise, returns false.
         *
         * @param target the string to be searched
         * @param patterns the patterns to search for
         * @return true if the target contains one of the patterns; false otherwise
         */
        fun containsOneOf(target: String, patterns: Set<String>): Boolean {
            if (patterns.isEmpty()) {
                return true
            }
            for (pattern in patterns) {
                if (target.contains(pattern)) {
                    return true
                }
            }
            return false
        }
    }
}
