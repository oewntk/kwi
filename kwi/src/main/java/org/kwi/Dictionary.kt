/*
 * Copyright (c) 2025.
 * Java Wordnet Interface Library (JWI) v2.4.0 Copyright (c) 2007-2015 Mark A. Finlayson
 * Kotlin Wordnet Interface Library (KWI) v1.0 Copyright (c) 2025 Bernard Bou
 * JWI is distributed under the terms of the Creative Commons Attribution 4.0 International Public License
 * KWI is distributed under the terms of the GPL3 License
 */

package org.kwi

import org.kwi.data.FileProvider
import java.io.File
import java.net.URL

/**
 * Basic `IDictionary` implementation that mounts files on disk and has
 * caching. A file URL to the directory containing the Wordnet dictionary files
 * must be provided.  This implementation has adjustable caching.
 */
class Dictionary : CachingDictionary {

    /**
     * Constructs a new dictionary that uses the Wordnet files located in a directory pointed to by the specified url
     *
     * @param wordnetDir an url pointing to a directory containing the Wordnet data files on the filesystem
     * @param config config parameters
     */
    @JvmOverloads
    constructor(wordnetDir: URL, config: Config? = null) : super(DataSourceDictionary(FileProvider(wordnetDir))) {
        configure(config)
    }

    /**
     * Constructs a new dictionary that uses the Wordnet files located in the specified directory
     *
     * @param wordnetDir a directory containing the Wordnet data files on the filesystem
     * @param config config parameters
     */
    @JvmOverloads
    constructor(wordnetDir: File, config: Config? = null) : super(DataSourceDictionary(FileProvider(wordnetDir))) {
        configure(config)
    }
}