package httpkit

import java.nio.charset.Charset

/**
 * Represents an HTTP request to be sent to a server.
 *
 * @property url The URL of the HTTP request.
 * @property headers A map of HTTP headers to be included in the request.
 * @property cookies A map of HTTP cookies to be included in the request.
 * @property body The body of the HTTP request, represented as a [Body]object.
 * @property query A map of query parameters to be appended to the URL.
 * @property method The HTTP method to be used for the request (e.g., GET, POST).
 * @property redirects Whether redirects should be followed automatically.
 * @property timeout The timeout for the request in milliseconds.
 */
data class HTTPRequest(
    override val url: String,
    override var headers: Map<String, List<String>> = emptyMap(),
    override var cookies: Map<String, String> = emptyMap(),
    override var body: Body = body {},
    var query: Map<String, String> = emptyMap(),
    var method: Method = Method.GET,
    var redirects: Boolean = true,
    var timeout: Int = 30000
) : HTTPMessage() {

    /**
     * Represents the body of an HTTP request.
     */
    class Body internal constructor() : HTTPMessage.Body() {

        /**
         * Represents the content of the HTTP request body.
         *
         * @property value The raw bytes of the content.
         */
        // Debug : value was internal
        class Content internal constructor(val value: ByteArray)

        /**
         * A collection of common HTTP content/mime type strings.
         */
        object ContentType {
            /**
             * Plain text content type.
             */
            const val TEXT_PLAIN = "text/plain"

            /**
             * HTML content type.
             */
            const val TEXT_HTML = "text/html"

            /**
             * XHTML content type.
             */
            const val APPLICATION_XHTML = "application/xhtml+xml"

            /**
             * XML content type.
             */
            const val APPLICATION_XML = "application/xml"

            /**
             * JSON content type.
             */
            const val APPLICATION_JSON = "application/json"

            /**
             * JavaScript content type.
             */
            const val APPLICATION_JAVASCRIPT = "application/javascript"

            /**
             * Form URL-encoded content type.
             */
            const val APPLICATION_FORM_URLENCODED= "application/x-www-form-urlencoded"

            /**
             * Octet-stream content type (for binary data).
             */
            const val APPLICATION_OCTET_STREAM = "application/octet-stream"

            /**
             * PDF content type.
             */
            const val APPLICATION_PDF = "application/pdf"

            /**
             * ZIP content type.
             */
            const val APPLICATION_ZIP = "application/zip"

            /**
             * GZIP content type.
             */
            const val APPLICATION_GZIP = "application/gzip"

            /**
             * Image JPEG content type.
             */
            const val IMAGE_JPEG = "image/jpeg"

            /**
             * Image PNG content type.
             */
            const val IMAGE_PNG = "image/png"

            /**
             * Image GIF content type.
             */
            const val IMAGE_GIF = "image/gif"

            /**
             * Image SVG content type.
             */
            const val IMAGE_SVG = "image/svg+xml"

            /**
             * Audio MPEG content type.
             */
            const val AUDIO_MPEG = "audio/mpeg"

            /**
             * Video MPEG content type.
             */
            const val VIDEO_MPEG = "video/mpeg"

            /**
             * Multipart form data content type.
             */
            const val MULTIPART_FORM_DATA = "multipart/form-data"

            /**
             * Form data content type with boundary.
             *
             * @param boundary The boundary string used to separate form data parts.
             */
            fun FormDataWithBoundary(boundary: String) = "multipart/form-data; boundary=$boundary"

        }

        /**
         * A builder class for creating form data to be used as the body of an HTTP request.
         *
         * This class extends [MutableMap] and provides convenient infix functions to add
         * key-value pairs using the "key to value" syntax.
         *
         * Example Usage (within a function that provides the appropriate context):**
         */
        class FormBuilder : MutableMap<String, String> by mutableMapOf() {

            /**
             * Adds a key-value pair to the form data, where the value is a [String].
             *
             * @param value The string value associated with the key.
             */
            infix fun String.to(value: String) = put(this, value)

            /**
             * Adds a key-value pair to the form data, where the value is any object.
             * The object's `toString()` representation will be used as the value.
             *
             * @param value The object value associated with the key.
             */
            infix fun String.to(value: Any) = put(this, value.toString())

        }

        /**
         * The content type of the request body (e.g., "text/plain", "application/json").
         */
        var contentType: String = ""

        /**
         * The character encoding used for the request body. Defaults to UTF-8.
         */
        var contentEncoding: Charset = Charsets.UTF_8

        /**
         * The actual content of the request body.
         */
        var content: Content = nothing()

        /**
         * Sets the content of the request body to empty.
         *
         * @return An empty [Content] object.
         */
        fun nothing(): Content = Content(byteArrayOf())

        /**
         *Sets the content of the request body to a string.
         *
         * @param value The string value to set as the content.
         * @return A [Content] object containing the string value.
         */
        fun string(value: String): Content = Content(value.toByteArray(contentEncoding)).also {
            if (contentType.isBlank()) contentType = "text/plain"
        }

        /**
         * Sets the content of the request body to a string using a lambda.
         *
         * @param block A lambda that returns the string value to set as the content.
         * @return A [Content] object containing the string value.
         */
        fun string(block: () -> String): Content = string(block())

        /**
         * Sets the content of the request body to a JSON string.
         *
         * @param value The JSON string to set as the content.
         * @return A [Content] object containing the JSON string.
         */
        fun json(value: String): Content = Content(value.toByteArray(contentEncoding)).also {
            if (contentType.isBlank()) contentType = "application/json"
        }

        /**
         * Sets the content of the request body to a JSON string using a lambda.
         *
         * @param block A lambda that returns the JSON string to set as the content.
         * @return A [Content] object containing the JSON string.
         */
        fun json(block: () -> String): Content = string(block())

        /**
         * Sets the content of the request body to an XML string.
         *
         * @param value The XML string to set as the content.
         * @return A [Content] object containing the XML string.
         */
        fun xml(value: String): Content = Content(value.toByteArray(contentEncoding)).also {
            if (contentType.isBlank()) contentType = "application/xml"
        }

        /**
         * Sets the content of the request body to an XML string using a lambda.
         *
         * @param block A lambda that returns the XML string to set as the content.
         * @return A [Content] object containing the XML string.
         */
        fun xml(block: () -> String): Content = string(block())

        /*** Sets the content of the request body to form data.
         *
         * @param block A lambda that builds a map of form fields.
         * @return A [Content] object containing the form data.
         */
        fun form(block: FormBuilder.() -> Unit): Content {

            if (contentType.isBlank()) contentType = "application/x-www-form-urlencoded"

            val fields: Map<String, String> = FormBuilder().apply(block)

            val bytes: ByteArray = fields.toList()
                .joinToString("&") { "${it.first}=${it.second}" }
                .toByteArray(contentEncoding)

            return Content(bytes)

        }

        /**
         * Sets the content of the request body to form data from an array of pairs.
         *
         * @param fields An array of pairs representing form fields.
         * @return A [Content] object containing the form data.
         */
        fun form(vararg fields: Pair<String, String>): Content = form {
            fields.forEach { field ->
                field.first to field.second
            }
        }

        /**
         * Sets the content of the request body to a file.
         *
         * @param value The byte array representing the file content.
         * @return A [Content] object containing the file content.
         */
        fun file(value: ByteArray): Content = Content(value).also {
            if (contentType.isBlank()) contentType = "application/octet-stream"
        }

        /**
         * Sets the content of the request body to a file using a lambda.
         *
         * @param block A lambda that returns the byte array representing the file content.
         * @return A [Content] object containing the file content.
         */
        fun file(block: () -> ByteArray): Content = file(block())

        /**
         * Sets the content of the request body to multipart data.
         *
         * @param block A lambda that builds a map of multipart parts.
         * @return A [Content] object containing the multipart data.
         */
        fun multipart(block: MutableMap<String, Body>.() -> Unit): Content {

            val bodyBoundary: String = "----HttpKitFormBoundary"

            contentType = "multipart/form-data; boundary=$bodyBoundary"

            val parts: Map<String, Body> = buildMap(block)

            val body: StringBuilder = StringBuilder()
            body.append("--$bodyBoundary\r\n")
            body.append(
                parts.toList().joinToString("--$bodyBoundary\r\n") {
                    "Content-Disposition: form-data;" +
                            "name=\"${it.first}\"\r\n\r\n" +
                            "${it.second.content.value.decodeToString()}\r\n"
                }
            )
            body.append("--$bodyBoundary\r\n--")

            val bytes: ByteArray = body.toString().toByteArray(contentEncoding)

            return Content(bytes)

        }

        /**
         * Sets the content of the request body to multipart data from an array of pairs.
         *
         * @param parts An array of pairs representing multipart parts.
         * @return A [Content] object containing the multipart data.
         */
        fun multipart(vararg parts: Pair<String, Body>): Content = multipart {
            parts.forEach { part ->
                part.first to part.second
            }
        }

    }

    /**
     * The HTTP methods supported for requests.
     */
    enum class Method { HEAD, GET, POST, PATCH, PUT, DELETE }

    /**
     * A listener for handling HTTP request events.
     */
    class Listener internal constructor() : HTTPListener<HTTPRequest>()

    companion object {

        /**
         * Creates a new [Body] object and allows for initialization using a lambda.
         *
         * @param init A lambda to initialize the [Body] object.
         * @return A new [Body] object initialized with the provided lambda.
         */
        fun body(init: Body.() -> Unit) = Body().apply(init)

    }

    /**
     * Creates a new [HTTPRequest] instance with the given URL and an initialization lambda.
     *
     * @param url The URL of the HTTP request.
     * @param init A lambda to initialize the [HTTPRequest] object.
     */
    constructor(url: String, init: HTTPRequest.() -> Unit) : this(url) {
        apply(init)
    }

}