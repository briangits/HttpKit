package httpkit

import java.io.IOException
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * The base class forall HTTP-related exceptions thrown by HttpKit.
 *
 * @property message A descriptive message about the error.
 * @property cause An optional underlying exception that caused this HTTP exception.
 */
sealed class HTTPException(
    override val message: String,
    override val cause: Throwable? = null
) : Exception(message, cause)

/**
 * Thrown when an HTTP request is canceled by an [HTTPListener].
 *
 * @property message A descriptive message about the cancellation.
 */
class HTTPRequestCancelledException(
    message: String
) : HTTPException(message)

/**
 * Thrown when the URL of an HTTP request is not properly formatted.
 *
 * @property message A descriptive message about the malformed URL.
 * @property cause The underlying [MalformedURLException].
 */
class MalformedHTTPURLException(
    message: String,
    cause: MalformedURLException
) : HTTPException(message, cause)

/**
 * Thrown when a timeout occurs while trying to establish an HTTP connection or
 * reading the response.
 *
 * @property message A descriptive message about the timeout.
 * @property cause The underlying [SocketTimeoutException].
 */
class HTTPTimeoutException(
    message: String,
    cause: SocketTimeoutException
) : HTTPException(message, cause)

/**
 * Thrown when an error occurs while establishing a socket connection for an HTTP request.
 *
 * @property message A descriptive message about the socket error.
 * @property cause The underlying [SocketException].
 */
class HTTPSocketException(
    message: String,
    cause: SocketException
) : HTTPException(message, cause)

/**
 * Thrown when there's an error related to the HTTP protocol, such as an invalid response from
 * the server, an unsupported HTTP method, or a mismatch in protocol versions.
 *
 * @property message A descriptive message about the protocol error.
 * @property cause The underlying [ProtocolException].
 */
class HTTPProtocolException(
    message: String,
    cause: ProtocolException
) : HTTPException(message, cause)

/**
 * Thrown when the host address of an HTTP request cannot be resolved to an IP address.
 *
 * @property message A descriptive message about the host resolution failure.
 * @property cause The underlying [UnknownHostException].
 */
class HTTPHostException(
    message: String,
    cause: UnknownHostException
) : HTTPException(message, cause)

/**
 * Thrown when a general IO error occurs during the HTTP request or response handling.
 *
 * @property message A descriptive message about the IO error.
 * @property cause The underlying [IOException].
 */
class HTTPIOException(
    message: String,
    cause: IOException
) : HTTPException(message, cause)