package org.kwi.data.parse

/**
 * A parser that transforms lines of data from a data source into data objects.
 *
 * @param <T> the type of the object into which this parser transforms lines
 */
interface ILineParser<T> {

    /**
     * Given the line of data, this method produces an object of class
     * `T`.
     *
     * @param line the line to be parsed
     * @return the object resulting from the parse
     * @throws MisformattedLineException if the line is malformed in some way
     **/
    fun parseLine(line: String): T
}

/**
 * Thrown when a line from a data resource does not match expected formatting conventions.
 */
class MisformattedLineException : RuntimeException {

    /**
     * Constructs a new exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a call to initCause.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the getMessage method.
     */
    constructor(message: String) : super(message)

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * Note that the detail message associated with `cause` is *not* automatically incorporated in this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage method).
     * @param cause the cause (which is saved for later retrieval by the getCause method). (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    constructor(message: String, cause: Throwable?) : super(message, cause)
    /**
     * Constructs a new exception with the specified cause and a detail message of `(cause==null ? null : cause.toString())` (which typically contains the class and detail message of `cause`).
     * This constructor is useful for runtime exceptions that are little more than wrappers for other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the getCause method). (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    constructor(cause: Throwable?) : super(cause)
}
