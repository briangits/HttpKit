package httpkit

sealed class HTTPError : Exception() {

    class MalformedURL() : HTTPError()

    class ProtocalNotSupported() : HTTPError()

    class ProtacalNotAllowed() : HTTPError()

    class ConnectionFailed() : HTTPError()

    class Timedout() : HTTPError()

    class Unknown(override val cause: Throwable) : HTTPError()

}