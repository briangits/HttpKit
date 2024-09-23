package httpkit.json

/**
 * Thrown when an error occurs during JSON parsing.
 */
class JSONParseException(message: String, cause: Throwable? = null) : Exception(message, cause)