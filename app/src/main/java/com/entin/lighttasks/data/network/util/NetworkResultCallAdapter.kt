package com.entin.lighttasks.data.network.util

import android.net.ConnectivityManager
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class NetworkResultCallAdapter<T : Any>(
    private val responseType: Type,
    private val connectivityManager: ConnectivityManager?
) : CallAdapter<T, Call<NetworkResult<T>>> {
    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<T>): Call<NetworkResult<T>> {
        return NetworkResultCall(call, connectivityManager)
    }
}
