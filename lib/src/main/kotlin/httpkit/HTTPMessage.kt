package httpkit

/**
 * A sealed class representing the base for all HTTP messages, such as requests and responses.
 */
sealed class HTTPMessage {

    /**
     * A sealed class representing the body of an HTTP message.*/
    sealed class Body

    /**
     * The URL associated with the HTTP message.
     */
    abstract val url: String

    /**
     * The headers associated with the HTTP message.
     */
    abstract val headers: Map<String, List<String>>

    /**
     * The cookies associated with the HTTP message.
     */
    abstract val cookies: Map<String, String>

    /**
     * The body of the HTTP message.
     */
    abstract val body: Body

}