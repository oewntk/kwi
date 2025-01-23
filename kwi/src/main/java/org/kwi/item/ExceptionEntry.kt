package org.kwi.item

import java.io.Serializable

/**
 * The data that can be obtained from a line in an exception entry file.
 * Because each exception entry does not specify its associated part-of-speech, this object is just a proxy and must be supplemented by the part-of-speech at some point to make a full ExceptionEntry object.
 */
open class ExceptionEntryProxy : Serializable {

    var surfaceForm: String

    var rootForms: Collection<String>

    /**
     * Constructs a new proxy that is a copy of the specified proxy
     *
     * @param proxy the proxy to be copied
     */
    constructor(proxy: ExceptionEntryProxy) {
        this.surfaceForm = proxy.surfaceForm
        this.rootForms = proxy.rootForms
    }

    /**
     * Constructs a new proxy with the specified field values.
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
class ExceptionEntry : ExceptionEntryProxy, IHasPOS, IItem<ExceptionKey> {

    override val pOS: POS

    override val iD: ExceptionKey

    val key: ExceptionKey
        get() = ExceptionKey(surfaceForm, pOS)

    /**
     * Creates a new exception entry for the specified part-of-speech using the information in the specified exception proxy object.
     *
     * @param proxy the proxy containing the information for the entry
     * @param pos the part-of-speech for the entry
     */
    constructor(proxy: ExceptionEntryProxy, pos: POS) : super(proxy) {
        this.pOS = pos
        this.iD = ExceptionKey(surfaceForm, pos)
    }

    override fun toString(): String {
        return "${super.toString()}-$pOS"
    }
}
