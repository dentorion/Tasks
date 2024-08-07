package com.entin.lighttasks.data.network.api

import com.entin.lighttasks.data.network.entity.TestRetrofitResponse
import com.entin.lighttasks.data.network.util.NetworkResult
import retrofit2.http.GET
import retrofit2.http.Path

interface TestApi {

    @GET("mobile/test/{numberCode}")
    suspend fun getAllModules(@Path("numberCode") numberCode: Int): NetworkResult<TestRetrofitResponse>
}
