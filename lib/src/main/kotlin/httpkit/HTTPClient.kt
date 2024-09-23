package httpkit

import java.io.IOException
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.SocketException
import java.nio.charset.Charset
import java.net.SocketTimeoutException
import java.net.URI
import java.net.UnknownHostException

/**
 * An HTTP client for sending HTTP requests and receiving HTTP responses.
 *
 * @property userAgent The user agent string to be sent with requests.
 * @property cookieManager The cookie manager to use for handling cookies.
 * @property listeners A list of listeners to be notified of HTTP events.
 */
class HTTPClient(
    var userAgent: String = "HttpKit HTTP Client",
    var cookieManager: HTTPCookieManager = HTTPCookieManager.defaultManager,
    var listeners: MutableList<HTTPListener<out HTTPMessage>> = mutableListOf()
) {

    /**
     * Creates an [HTTPClient] instance with an initialization lambda.
     *
     * @param init A lambda to initialize the [HTTPClient] instance.
     */
    constructor(init: HTTPClient.() -> Unit) : this() { apply(init) }

    /**
     * Sends an HTTP request and returns the HTTP response.
     *
     * @param request The HTTP request to send.
     * @return The HTTP response received from the server.
     * @throws HTTPException If an error occurs during the request or response handling.
     */
    fun send(request: HTTPRequest): HTTPResponse {

        // Merge cookies from the cookie manager and the request
        val cookies = cookieManager[request.url.toSite()].toMutableMap()
        cookies.putAll(request.cookies)
        request.cookies = cookies

        // Notify request listeners
        request.qualifyingListeners.forEach { it.intercept(request) }

        val response: HTTPResponse = try {
            sendRequest(request)} catch (e: Exception) {
            // Handle exceptions and throw appropriate HTTPException
            throw when (e) {
                is MalformedURLException ->
                    MalformedHTTPURLException("Invalid URL: ${request.url}", e)
                is SocketTimeoutException -> HTTPTimeoutException("Request timed out", e)
                is ConnectException -> HTTPSocketException("Connection failed: ${e.message}", e)
                is SocketException -> HTTPSocketException("Socket error: ${e.message}", e)
                is ProtocolException -> HTTPProtocolException("Protocol error: ${e.message}", e)
                is UnknownHostException -> HTTPHostException("Host not found: ${request.url}", e)
                is IOException -> HTTPIOException("IO error during request: ${e.message}", e)
                else -> e // Re-throw unknown exceptions
            }
        }

        // Notify response listeners
        response.qualifyingListeners.forEach { it.intercept(response) }

        // Handle retries if requested by a listener
        if (response.qualifyingListeners.any { it.retryAfterAction }) {
            return send(request) // Retry the request
        }

        cookieManager.handleCookies(request.url.toSite(), response.cookies)

        // Handle redirects
        if (response.status in 300..399 && request.redirects) {
            return handleRedirects(request, response)
        }

        return response

    }

    // Handles redirect responses
    private fun handleRedirects(
        sentRequest: HTTPRequest,
        receivedResponse: HTTPResponse
    ): HTTPResponse {

        // Extract the location header or return the response if blank or not found
        val location: String = receivedResponse.headers["location"]?.firstOrNull()
            ?.takeIf { it.isNotBlank() }
            // Make the location absolute if relative
            ?.let {
                if (it.startsWith("/")) {
                    "${sentRequest.url.substringBeforeLast('/') }$it"
                } else it
            } ?: return receivedResponse

        // Create a new GET Request with the new URL
        val request: HTTPRequest = HTTPRequest(location) {
            redirects = true
        }

        // Debug
        println("Redirecting to $location")

        // Copy the request cookies
        request.cookies = sentRequest.cookies.toMutableMap()

        // Recursively send the new request
        return send(request)

    }

    // Extracts the host site from a URL string
    private fun String.toSite(): String = URI.create(this).host

    // Sends an HTTP request and returns the response, handling exceptions internally
    private fun sendRequest(request: HTTPRequest): HTTPResponse {
        val connection = with(request.toURLConnection()) {
            connect()
            this
        }
        val response: HTTPResponse = connection.toHTTPResponse()
        val site: String = connection.url.host
        cookieManager.handleCookies(site, response.cookies)
        return response
    }

    // Converts an HTTPRequest to a HttpURLConnection
    private fun HTTPRequest.toURLConnection(): HttpURLConnection {

        val query: String = if (this.query.isNotEmpty()) {
            "?${this.query.toList().joinToString("&") { "${it.first}=${it.second}" }}"
        } else ""

        val connection: HttpURLConnection = URI(this.url + query).toURL().openConnection()
                as HttpURLConnection
        connection.setRequestProperty("User-Agent", userAgent)
        connection.setRequestProperty("Accept", "*/*")

        this.headers.forEach { (key, values) ->
            values.forEach { connection.setRequestProperty(key, it) }
        }

        this.cookies.forEach { (name, value) ->
            connection.setRequestProperty("Cookie", "$name=$value")
        }

        // Configure follow redirects
        connection.instanceFollowRedirects = false

        // Set connection timeout
        connection.connectTimeout = this.timeout

        connection.requestMethod = this.method.name

        if (this.method == HTTPRequest.Method.HEAD && this.method == HTTPRequest.Method.GET)
            return connection

        if(this.body.content == this.body.nothing()) return connection

        val body: ByteArray = this.body.content.value

        connection.doOutput = true
        connection.outputStream.use { outputStream ->
            outputStream.write(body)
            outputStream.flush()
        }

        return connection
    }

    // Converts a HttpURLConnection to an HTTPResponse
    private fun HttpURLConnection.toHTTPResponse(): HTTPResponse {
        val status: Int = this.responseCode
        val url: String = this.url.toString().lowercase()
        val headers: Map<String, List<String>> = this.headerFields.filterKeys { it != null }
            .mapKeys { it.key.lowercase() }.mapValues { it.value.toList() }.toMap()

        val cookies: Map<String, String> = buildMap {
            headers["set-cookie"]?.forEach { cookie ->
                val pair: String = cookie.split(";")[0].trim()
                if (pair.split("=").size == 2) {
                    val (name, value) = pair.split("=")
                    this[name.trim()] = value.trim()
                }
            }
        }

        val bytes: ByteArray = try {
            this.inputStream.use { it.readBytes() } // Use 'use' to ensure stream is closed
        } catch (e: Exception) {
            // Handle potential errors while reading the response body
            byteArrayOf() // Return an empty byte array if an error occurs
        }

        val body: HTTPResponse.Body = HTTPResponse.Body(bytes)
        //body.contentType = this.contentType
        body.contentEncoding = this.contentEncoding.runCatching {
            Charset.forName(this)
        }.getOrElse { Charsets.UTF_8 }

        return HTTPResponse(status, url, headers, cookies, body)

    }

    // Gets the qualifying HTTPRequest.Listener's for a given request
    private val HTTPRequest.qualifyingListeners: List<HTTPRequest.Listener>
        get() = listeners.filterIsInstance<HTTPRequest.Listener>().filter { it.qualifies(this) }

    // Intercepts an HTTP request using a HTTPRequest.Listener
    private fun HTTPRequest.Listener.intercept(request: HTTPRequest) {

        if (this.cancel) throw HTTPRequestCancelledException(
            """
                The Request was intercepted and cancelled by Request-Listener with
                   ID: ${this.id}
                   Tag: ${this.tag}
                Request Details
                URL: ${request.url}
                Header Fields: [${request.headers.keys.joinToString(", ")}]
                Cookie Names: [${request.cookies.keys.joinToString(", ")}]
                Method: ${request.method.name}
                Request Body Details
                   Content-Type: ${request.body.contentType}
                   Size: ${request.body.content.value.size}b
                Follows Redirects: ${request.redirects}
                Timeout: ${request.timeout}ms
            """.trimIndent()
        )

        this.action(request)

    }

    // Gets the qualifying HTTPResponse.Listener's for a given response
    private val HTTPResponse.qualifyingListeners: List<HTTPResponse.Listener>
        get() = listeners.filterIsInstance<HTTPResponse.Listener>().filter { it.qualifies(this) }

    // Intercepts an HTTP response using a HTTPResponse.Listener
    private fun HTTPResponse.Listener.intercept(response: HTTPResponse) {

        if (this.cancel) throw HTTPRequestCancelledException(
            """
                The Request was intercepted and cancelled by Response-Listener with
                   ID: ${this.id}Tag: ${this.tag}
                Response Details
                URL: ${response.url}
                Status: ${response.status}
                Header Fields: [${response.headers.keys.joinToString(", ")}]
                Cookie Names: [${response.cookies.keys.joinToString(", ")}]
                Response Body Details
                   Content-Type: ${response.body.contentType}
                   Content-Encoding: ${response.body.contentEncoding.name()}
                   Size: ${response.body.bytes.size}b
            """.trimIndent()
        )

        this.action(response)

    }

    // Handles cookies for a given site using the HTTPCookieManager
    private fun HTTPCookieManager.handleCookies(site: String, cookies: Map<String, String>) {

        val cookiesToRemove: List<String> = cookies.filter { it.value.isEmpty() }.keys.toList()
        remove(site, cookiesToRemove)

        val cookiesToSet: Map<String, String> = cookies.filter { it.key !in cookiesToRemove }
        set(site, cookiesToSet)

    }

}