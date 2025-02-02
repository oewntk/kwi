/*
 * Copyright (c) 2025.
 * Java Wordnet Interface Library (JWI) v2.4.0 Copyright (c) 2007-2015 Mark A. Finlayson
 * Kotlin Wordnet Interface Library (KWI) v1.0 Copyright (c) 2025 Bernard Bou
 * JWI is distributed under the terms of the Creative Commons Attribution 4.0 International Public License
 * KWI is distributed under the terms of the GPL3 License
 */

package org.kwi.data

/**
 * An object that can be closed. What 'closing' means is implementation specific.
 */
interface IClosable {

    /**
     * This closes the object by disposing of data backing objects or connections.
     * If the object is already closed, or in the process of closing, this method does nothing (although, if the object is in the process of closing, it may block until closing is complete).
     */
    fun close()
}
