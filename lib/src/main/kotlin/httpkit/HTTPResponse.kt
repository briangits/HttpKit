package httpkit

import httpkit.html.HTMLDocument
import httpkit.html.HTMLParser
import httpkit.json.JsonParser
import java.nio.charset.Charset

/**
 * Represents an HTTP response received from a server.
 *
 * @property status The HTTP status code of the response (e.g., 200 for OK, 404 for Not Found).
 * @property url The URL that the response was received from.
 * @property headers A map of HTTP headers, where the key is the header name and the value is a
 * list of header values.
 * @property cookies A map of HTTP cookies, where the key is the cookie name and the value is
 * the cookie value.
 * @property body The body of the HTTP response, represented as a [Body] object.
 */
data class HTTPResponse(
    val status: Int,
    override val url: String,
    override val headers: Map<String, List<String>>,
    override val cookies: Map<String, String>,
    override val body: Body
) : HTTPMessage() {

    /**
     * Represents the body of an HTTP response.
     *
     * @property bytes The raw bytes of the response body.
     * @property string A lazily computed string representation of the response body,
     * decoded using the [contentEncoding].
     * @property contentType The content type of the response body
     * (e.g., "text/html", "application/json").
     * @property contentEncoding The character encoding used to decode the response body.
     * Defaults to UTF-8.
     */
    class Body internal constructor(val bytes: ByteArray) : HTTPMessage.Body() {

        /**
         * Creates a new [Body] instance from the provided byte array and allows for initialization
         * using the [init] lambda.
         *
         * @param bytes The raw bytes of the response body.
         * @param init An initialization lambda to configure the [Body] object.
         */
        constructor(bytes: ByteArray, init: Body.() -> Unit) : this(bytes) {
            apply(init)
        }

        val string: String by lazy { bytes.decodeToString() }

        var contentType: String = "application/octet-stream"
            internal set

        var contentEncoding: Charset = Charsets.UTF_8
            internal set

        /**
         * Parses the response body as an HTML document.
         *
         * @return An [HTMLDocument] representing the parsed HTML content.
         */
        fun toHTMLDocument(): HTMLDocument = HTMLParser.parse(string)

        /**
         * Parse the response body as a JSON Object
         *
         * @return A [Map] representing the parsed JSON content.
         */
        fun toJSONMap(): Map<String, Any?> = JsonParser.parseToMap(string)

        /**
         * Parse the response body as a JSON Array
         *
         * @return A [List] representing the parsed JSON content.
         */
        fun toJSONList(): List<Any?> = JsonParser.parseToList(string)

    }

    /**
     * A listener for handling HTTP response events.
     */
    class Listener internal constructor() : HTTPListener<HTTPResponse>()

}