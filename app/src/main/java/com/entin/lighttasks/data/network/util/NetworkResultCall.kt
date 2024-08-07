package com.entin.lighttasks.data.network.util

import android.net.ConnectivityManager
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

/** Maps a [Call] to [NetworkResultCall] */
class NetworkResultCall<T : Any>(
    private val call: Call<T>,
    private val connectivityManager: ConnectivityManager?
) : Call<NetworkResult<T>> {

    override fun enqueue(callback: Callback<NetworkResult<T>>) {
        call.enqueue(
            object : Callback<T> {
                override fun onResponse(
                    call: Call<T>,
                    response: Response<T>
                ) {
                    if (call.isCanceled) return

                    if (response.isSuccessful) {
                        if (response.body() == null) {
                            callback.onResponse(
                                this@NetworkResultCall,
                                Response.success(
                                    NetworkResult.Failure(
                                        RemoteServiceHttpError(response)
                                    )
                                )
                            )
                        } else {
                            callback.onResponse(
                                this@NetworkResultCall,
                                Response.success(
                                    NetworkResult.Success(response.body()!!)
                                )
                            )
                        }
                    } else {
                        callback.onResponse(
                            this@NetworkResultCall,
                            Response.success(
                                NetworkResult.Failure(
                                    RemoteServiceHttpError(response)
                                )
                            )
                        )
                    }
                }

                override fun onFailure(
                    call: Call<T>,
                    throwable: Throwable
                ) {
                    val error: Error =
                        if (throwable is IOException) {
                            NetworkError(
                                connectivityManager?.activeNetworkInfo?.isConnected ?: false,
                                throwable
                            )
                        } else {
                            UnexpectedError(throwable)
                        }

                    callback.onResponse(
                        this@NetworkResultCall,
                        Response.success(
                            NetworkResult.Failure(error)
                        )
                    )
                }
            }
        )
    }

    override fun execute(): Response<NetworkResult<T>> {
        throw (IllegalStateException("execute function is not supported"))
    }

    override fun isExecuted(): Boolean {
        return call.isExecuted
    }

    override fun clone(): Call<NetworkResult<T>> {
        return NetworkResultCall(
            call.clone(),
            connectivityManager = connectivityManager
        )
    }

    override fun cancel() {
        call.cancel()
    }

    override fun isCanceled(): Boolean {
        return call.isCanceled
    }

    override fun request(): Request {
        return call.request()
    }

    override fun timeout(): Timeout {
        return call.timeout()
    }
}