/*
 * Copyright (c) 2025.
 * Java Wordnet Interface Library (JWI) v2.4.0 Copyright (c) 2007-2015 Mark A. Finlayson
 * Kotlin Wordnet Interface Library (KWI) v1.0 Copyright (c) 2025 Bernard Bou
 * JWI is distributed under the terms of the Creative Commons Attribution 4.0 International Public License
 * KWI is distributed under the terms of the GPL3 License
 */

package org.kwi.data

import org.kwi.item.POS

/**
 * Content type keys.
 */
enum class ContentTypeKey(
    val dataType: DataType<*>,
    val pOS: POS?,
) {

    INDEX_NOUN(DataType.INDEX, POS.NOUN),
    INDEX_VERB(DataType.INDEX, POS.VERB),
    INDEX_ADVERB(DataType.INDEX, POS.ADVERB),
    INDEX_ADJECTIVE(DataType.INDEX, POS.ADJECTIVE),
    DATA_NOUN(DataType.DATA, POS.NOUN),
    DATA_VERB(DataType.DATA, POS.VERB),
    DATA_ADVERB(DataType.DATA, POS.ADVERB),
    DATA_ADJECTIVE(DataType.DATA, POS.ADJECTIVE),
    EXCEPTION_NOUN(DataType.EXCEPTION, POS.NOUN),
    EXCEPTION_VERB(DataType.EXCEPTION, POS.VERB),
    EXCEPTION_ADVERB(DataType.EXCEPTION, POS.ADVERB),
    EXCEPTION_ADJECTIVE(DataType.EXCEPTION, POS.ADJECTIVE),
    SENSE(DataType.SENSE, null);
}