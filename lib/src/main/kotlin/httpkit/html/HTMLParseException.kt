package httpkit.html

/**
 * Base class for all HTML parsing exceptions.
 */
sealed class HTMLParseException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * Thrown when attempting to parse empty or null HTML content.
 */
class EmptyHtmlException : HTMLParseException("HTML content cannot be empty")

/**
 * Thrown when an error occurs during the HTML parsing process.
 */
class HtmlParsingException(
    cause: Throwable
) : HTMLParseException("Error parsing HTML content", cause)