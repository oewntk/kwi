package org.kwi.data

import java.io.IOException

/**
 * Object that has a lifecycle.
 * Objects implementing this interface can be opened, closed, and have their open state queried.
 * In general, the open state of the object should be reversible, that is, an object may be closed and re-opened.
 * What happens when the object is used when closed is implementation dependent.
 */
interface IHasLifecycle : IClosable {

    /**
     * This opens the object by performing any required initialization steps.
     * If this method returns false, then subsequent calls to isOpen will return false.
     *
     * @return true if there were no errors in initialization; false otherwise.
     * @throws IOException if there was IO error while performing initialization
     */
    @Throws(IOException::class)
    fun open(): Boolean

    /**
     * Returns true if the dictionary is open, that is, ready to accept queries; returns false otherwise
     *
     * @return true if the object is open; false otherwise
     */
    val isOpen: Boolean

    /**
     * An enum that represents the four different lifecycle states an object may be in.
     * It may be closed, open, in the processing of opening, or in the process of closing.
     */
    enum class LifecycleState {

        CLOSED, OPENING, OPEN, CLOSING
    }

    /**
     * Indicates that the object was closed when some method was called requiring it to be open.
     */
    class ObjectClosedException : RuntimeException {

        /**
         * Constructs a new exception with null as its detail message.
         * The cause is not initialized, and may subsequently be initialized by a call to initCause.
         */
        constructor() : super()

        /**
         * Constructs a new exception with the specified detail message.
         * The cause is not initialized, and may subsequently be initialized by a call to initCause.
         *
         * @param message the detail message. The detail message is saved for later retrieval by the getMessage method.
         */
        constructor(message: String?) : super(message)

        /**
         * Constructs a new exception with the specified cause and a detail message of `(cause==null ? null : cause.toString())` (which typically contains the class and detail message of `cause`).
         * This constructor is useful for runtime exceptions that are little more than wrappers for other throwables.
         *
         * @param cause the cause (which is saved for later retrieval by the getCause method). (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
         */
        constructor(cause: Throwable?) : super(cause)
    }

    /**
     * Indicates that the object was open when some method was called requiring it to be closed.
     */
    class ObjectOpenException : RuntimeException {

        /**
         * Constructs a new exception with null as its detail message. The cause is not initialized, and may subsequently be initialized by a call to initCause.
         */
        constructor() : super()

    }
}
