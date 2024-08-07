package com.entin.lighttasks.data.network.util

import retrofit2.Response

fun Throwable.userDescription(): String {
    return (this as? Error)?.userDescription() ?: this.message ?: "An unknown error occurred."
}

fun Error.userDescription(): String {
    return when (this) {
        is RemoteServiceHttpError -> {
            when {
                isServerError -> "A server error occurred. Code: (${httpStatusCode.code})"
                isClientError -> "An error occurred. Code: (${httpStatusCode.code})"
                isEmptyBody -> "Response body is empty. Code: (${httpStatusCode.code})"
                else -> "Something goes wrong. Code: (${httpStatusCode.code})"
            }
        }

        is NetworkError -> {
            if (hasInternetConnectivity) {
                "Data from server could not be reached. Reason: $this"
            } else {
                "You are not connected to the internet."
            }
        }

        is UnexpectedError -> "An unexpected error occurred. Reason: $this"
        else -> "An unknown error occurred."
    }
}


class UnexpectedError(val t: Throwable) : Error() {
    override fun toString(): String {
        return "UnexpectedError (${t.localizedMessage})"
    }
}

/**
 * Represents an error in which the server could not be reached.
 */
class NetworkError(val hasInternetConnectivity: Boolean, val t: Throwable) : Error() {
    override fun toString(): String {
        return "NetworkError (hasInternetConnectivity: $hasInternetConnectivity, error: ${t.localizedMessage})"
    }
}

/**
 * A Remote Service Error with a HttpStatus code.
 */
class RemoteServiceHttpError(private val response: Response<*>) : Error() {
    val httpStatusCode: HttpStatusCode = HttpStatusCode.values().firstOrNull { statusCode ->
            statusCode.code == response.code()
        } ?: HttpStatusCode.Unknown

    val isClientError: Boolean
        get() = response.code() in 400..499

    val isServerError: Boolean
        get() = response.code() in 500..599

    val isEmptyBody: Boolean
        get() = response.code() == 200 && response.body() == null

    override fun toString() = "RemoteServiceHttpError" +
                "\n\t- code: ${response.code()} (${httpStatusCode.name})" +
                "\n\t- message: ${super.message}"
}

enum class HttpStatusCode(val code: Int) {
    Unknown(-1),

    // Client Errors
    BadRequest(400),
    Unauthorized(401),
    PaymentRequired(402),
    Forbidden(403),
    NotFound(404),
    MethodNotAllowed(405),
    NotAcceptable(406),
    ProxyAuthenticationRequired(407),
    RequestTimeout(408),
    Conflict(409),
    Gone(410),
    LengthRequired(411),
    PreconditionFailed(412),
    PayloadTooLarge(413),
    UriTooLong(414),
    UnsupportedMediaType(415),
    RangeNotSatisfiable(416),
    ExpectationFailed(417),
    ImATeapot(418),
    MisdirectedRequest(421),
    UnprocessableEntity(422),
    Locked(423),
    FailedDependency(424),
    UpgradeRequired(426),
    PreconditionRequired(428),
    TooManyRequests(429),
    RequestHeaderFieldsTooLarge(431),
    UnavailableForLegalReasons(451),

    // Server Errors
    InternalServerError(500),
    NotImplemented(501),
    BadGateway(502),
    ServiceUnavailable(503),
    GatewayTimeout(504),
    HttpVersionNotSupported(505),
    VariantAlsoNegates(506),
    InsufficientStorage(507),
    LoopDetected(508),
    NotExtended(510),
    NetworkAuthenticationRequired(511);
}