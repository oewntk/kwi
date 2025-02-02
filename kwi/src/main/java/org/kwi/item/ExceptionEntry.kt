/*
 * Copyright (c) 2025.
 * Java Wordnet Interface Library (JWI) v2.4.0 Copyright (c) 2007-2015 Mark A. Finlayson
 * Kotlin Wordnet Interface Library (KWI) v1.0 Copyright (c) 2025 Bernard Bou
 * JWI is distributed under the terms of the Creative Commons Attribution 4.0 International Public License
 * KWI is distributed under the terms of the GPL3 License
 */

package org.kwi.item

import java.io.Serializable

/**
 * The data that can be obtained from a line in an exception entry file.
 * Because each exception entry does not specify its associated part-of-speech, this object is just a proto entry and must be supplemented by the part-of-speech at some point to make a full ExceptionEntry object.
 */
open class ExceptionProtoEntry : Serializable {

    var surfaceForm: String

    var rootForms: Collection<String>

    /**
     * Constructs a new proto entry that is a copy of the specified entry
     *
     * @param proto the proto to be copied
     */
    constructor(proto: ExceptionProtoEntry) {
        this.surfaceForm = proto.surfaceForm
        this.rootForms = proto.rootForms
    }

    /**
     * Constructs a new proto entry with the specified field values.
     *
     * @param surfaceForm the surface form for the entry; may not be empty, or all whitespace
     * @param rootForms the root forms for the entry; may not contain null, empty, or all whitespace strings
     */
    constructor(surfaceForm: String, rootForms: Collection<String>) {
        this.surfaceForm = surfaceForm
        this.rootForms = rootForms
            .map { it.trim { it <= ' ' } }
            .also { it.isNotEmpty() }
            .toList()
    }

    override fun toString(): String {
        return "EXC-$surfaceForm[${rootForms.joinToString(separator = ", ")}]"
    }
}

/**
 * Exception Entry
 */
class ExceptionEntry : ExceptionProtoEntry, IHasPOS, IItem<ExceptionKey> {

    override val pOS: POS

    override val iD: ExceptionKey

    val key: ExceptionKey
        get() = ExceptionKey(surfaceForm, pOS)

    /**
     * Creates a new exception entry for the specified part-of-speech using the information in the specified exception proto entry object.
     *
     * @param proto the proto containing the information for the entry
     * @param pos the part-of-speech for the entry
     */
    constructor(proto: ExceptionProtoEntry, pos: POS) : super(proto) {
        this.pOS = pos
        this.iD = ExceptionKey(surfaceForm, pos)
    }

    override fun toString(): String {
        return "${super.toString()}-$pOS"
    }
}
