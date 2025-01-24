package org.kwi.data

import org.kwi.data.compare.*
import org.kwi.item.*

/**
 * Objects that represent all possible types of content
 * that are contained in the dictionary data resources.
 * Each unique object of this type will correspond to a particular resource or file.
 *
 * In the standard Wordnet distributions, examples of content types would include, but would not be limited to,
 * *Index*,
 * *Data*, and
 * *Exception*
 * files for each part-of-speech.
 *
 * This class provides the content types necessary for Wordnet in the form of static fields.
 * It is not implemented as an `Enum` so that clients may add their own content types by instantiating this class.
 *
 * @param <T> the type of object for the content type
 * @property key content type key
 * @property lineComparator the line comparator for this content type; may be null if the lines are not ordered
 * @param charset the character set for this content type
 */
class ContentType<T>(
    /**
     * Content type key
     */
    val key: ContentTypeKey,

    /**
     * Comparator that can be used to determine ordering between different lines of data in the resource.
     * It imposes an ordering on the lines in the data file
     * This is used for searching
     * If the data in the resource is not ordered, then this property is null.
     */
    val lineComparator: ILineComparator?,

    ) : IHasPOS {

    val dataType: DataType<T>
        get() {
            @Suppress("UNCHECKED_CAST")
            return key.dataType as DataType<T>
        }

    override val pOS: POS?
        get() {
            return key.pOS
        }

    override fun toString(): String {
        return if (key.pOS != null) {
            "[ContentType: ${key.dataType}/${key.pOS}]"
        } else {
            "[ContentType: ${key.dataType}]"
        }
    }

    companion object {

        val INDEX_NOUN = ContentType<Index>(ContentTypeKey.INDEX_NOUN, IndexLineComparator)
        val INDEX_VERB = ContentType<Index>(ContentTypeKey.INDEX_VERB, IndexLineComparator)
        val INDEX_ADVERB = ContentType<Index>(ContentTypeKey.INDEX_ADVERB, IndexLineComparator)
        val INDEX_ADJECTIVE = ContentType<Index>(ContentTypeKey.INDEX_ADJECTIVE, IndexLineComparator)

        val DATA_NOUN = ContentType<Synset>(ContentTypeKey.DATA_NOUN, DataLineComparator)
        val DATA_VERB = ContentType<Synset>(ContentTypeKey.DATA_VERB, DataLineComparator)
        val DATA_ADVERB = ContentType<Synset>(ContentTypeKey.DATA_ADVERB, DataLineComparator)
        val DATA_ADJECTIVE = ContentType<Synset>(ContentTypeKey.DATA_ADJECTIVE, DataLineComparator)

        val EXCEPTION_NOUN = ContentType<ExceptionEntryProxy>(ContentTypeKey.EXCEPTION_NOUN, ExceptionLineComparator)
        val EXCEPTION_VERB = ContentType<ExceptionEntryProxy>(ContentTypeKey.EXCEPTION_VERB, ExceptionLineComparator)
        val EXCEPTION_ADVERB = ContentType<ExceptionEntryProxy>(ContentTypeKey.EXCEPTION_ADVERB, ExceptionLineComparator)
        val EXCEPTION_ADJECTIVE = ContentType<ExceptionEntryProxy>(ContentTypeKey.EXCEPTION_ADJECTIVE, ExceptionLineComparator)

        val SENSE = ContentType<SenseEntry>(ContentTypeKey.SENSE, SenseKeyLineComparator)

        // set of all content types implemented in this class
        private val contentTypes: Set<ContentType<*>> = setOf(
            INDEX_NOUN,
            INDEX_VERB,
            INDEX_ADVERB,
            INDEX_ADJECTIVE,
            DATA_NOUN,
            DATA_VERB,
            DATA_ADVERB,
            DATA_ADJECTIVE,
            EXCEPTION_NOUN,
            EXCEPTION_VERB,
            EXCEPTION_ADVERB,
            EXCEPTION_ADJECTIVE,
            SENSE,
        )

        /**
         * Emulates the Enum.values() function.
         *
         * @return all the ContentType instances listed in the class, in the order they are declared.
         */
        @JvmStatic
        fun values(): Collection<ContentType<*>> {
            return contentTypes
        }
    }
}
