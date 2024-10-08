package httpkit

import java.util.UUID

/**
 * A sealed class representing a listener for HTTP messages (requests or responses).
 *
 * @param T The type of HTTP message this listener handles, either [HTTPRequest] or [HTTPResponse].
 */
sealed class HTTPListener<T : HTTPMessage> {

    companion object {

        /**
         * Creates an [HTTPListener] for [HTTPRequest] and allows for initialization using
         * the [init] lambda.
         *
         * @param init A lambda to initialize the [HTTPRequest.Listener] object.
         * @return A new [HTTPListener] for [HTTPRequest] initialized with the provided lambda.
         */
        fun ofRequest(init: HTTPRequest.Listener.() -> Unit): HTTPListener<HTTPRequest> =
            HTTPRequest.Listener().apply(init)

        /**
         * Creates an [HTTPListener] for [HTTPResponse] and allows for initialization using the [init] lambda.
         *
         * @param init A lambda to initialize the [HTTPResponse.Listener]object.
         * @return A new [HTTPListener] for [HTTPResponse] initialized with the provided lambda.
         */
        fun ofResponse(init: HTTPResponse.Listener.() -> Unit): HTTPListener<HTTPResponse> =
            HTTPResponse.Listener().apply(init)

    }

    /**
     * A unique identifier for the listener, generated by default using a UUID.
     */
    var id: String = "defID-${UUID.randomUUID().toString()}"

    /**
     * An optional tag for the listener.
     */
    var tag: String = ""

    /**
     * A condition lambda that determines whether the listener should be triggered
     * for a given HTTP message.
     * It receives the HTTP message as its context and should return `true` if the listener
     * should be triggered, `false` otherwise.
     */
    var condition: (message: T) -> Boolean = { false }

    /**
     * The action lambda to be performed when the listener is triggered.
     * It receives the HTTP message that triggered the listener as its context.
     */
    var action: (message: T) -> Unit = {}

    /**
     * Whether the HTTP request or response associated with this listener should be canceled.
     */
    var cancel: Boolean = false

    /**
     * Whether the HTTP request should be retried after the [action] is performed.
     */
    var retryAfterAction: Boolean = false

    /**
     * Checks if the listener qualifies for the given HTTP message based on the [condition].
     *
     * @param message The HTTP message to check.
     * @return `true` if the listener qualifies, `false` otherwise.
     */
    internal fun qualifies(message: T): Boolean = condition(message)

}