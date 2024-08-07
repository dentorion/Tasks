package com.entin.lighttasks.data.network.util

sealed class NetworkResult<out T : Any> {
    data class Success<T : Any>(val body: T) : NetworkResult<T>()
    data class Failure(val error: Throwable) : NetworkResult<Nothing>()
}