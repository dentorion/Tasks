package com.entin.lighttasks.presentation.screens.preferencies

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.entin.lighttasks.data.network.entity.TestRetrofitResponse
import com.entin.lighttasks.data.network.util.NetworkResult
import com.entin.lighttasks.data.network.util.userDescription
import com.entin.lighttasks.domain.repository.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class PreferencesViewModel @Inject constructor(
    val state: SavedStateHandle,
    private val taskRepository: TasksRepository,
) : ViewModel() {

    fun launchTest() {
        viewModelScope.launch {
//            val a = launch {
//                Log.e("RESULT", "A start")
//                delay(15000L)
//                Log.e("RESULT", "A result")
//            }
//
//            val b = launch {
//                Log.e("RESULT", "B start")
//                delay(5000L)
//                Log.e("RESULT", "B result")
//            }

//            a.join()
//            b.join()


            Log.e("RESULT", "A00 start")
            delay(1000L)
            Log.e("RESULT", "A00 result")

            Log.e("RESULT", "B11 start")
            delay(1500L)
            Log.e("RESULT", "B11 result")

            launch {
                Log.e("RESULT", "1 start")
                delay(1000L)
                Log.e("RESULT", "1 result")
            }

            launch {
                Log.e("RESULT", "2 start")
                delay(1500L)
                Log.e("RESULT", "2 result")
            }

            launch {
                Log.e("RESULT", "3 start")
                delay(100L)
                Log.e("RESULT", "3 result")
            }

            launch {
                Log.e("RESULT", "4 start")
                delay(2000L)
                Log.e("RESULT", "4 result")
            }

            launch {
                Log.e("RESULT", "5 start")
                delay(1500L)
                Log.e("RESULT", "5 result")
            }
        }
    }

    fun asyncTest() {
        val handle = CoroutineExceptionHandler { _, e ->
            Log.e("RESULT", "Exception handled")
        }
        viewModelScope.launch(handle) {
            val first = async {
                delay(2500)
                Log.e("RESULT", "1")
                "1"
            }
            val second = async {
                delay(5000)
                Log.e("RESULT", "2")
                "2"
            }
            val third = async {
                delay(9500)
                Log.e("RESULT", "3")
                "3"
            }

            val one = try {
                first.await()
            } catch (e: Exception) {
                null
            }
            val two = try {
                second.await()
            } catch (e: Exception) {
                null
            }
            val three = try {
                third.await()
            } catch (e: Exception) {
                null
            }

            val result = listOfNotNull(one, two, three)
            Log.i("RESULT", "!!!!!!!!! RESULT: $result")
        }
    }

    // Open section
    fun getTestHttpResponse() {
        viewModelScope.launch {
            val result200 = withContext(Dispatchers.IO) {
                taskRepository.getTestHttpResponse(200)
            }

            println("===========================")
            when (result200) {
                is NetworkResult.Success -> {
                    println("Success ${result200.body.projectId}")
                }

                is NetworkResult.Failure -> {
                    println(result200.error.userDescription())
                }
            }
            println("===========================")

            delay(1000L)

            val result403 = withContext(Dispatchers.IO) {
                taskRepository.getTestHttpResponse(403)
            }

            println("===========================")
            when (result403) {
                is NetworkResult.Success<TestRetrofitResponse> -> {
                    println("Success ${result403.body.projectId}")
                }

                is NetworkResult.Failure -> {
                    println(result403.error.userDescription())
                }
            }
            println("===========================")

            delay(1000L)

            val result502 = withContext(Dispatchers.IO) {
                taskRepository.getTestHttpResponse(502)
            }

            println("===========================")
            when (result502) {
                is NetworkResult.Success<TestRetrofitResponse> -> {
                    println("Success ${result502.body.projectId}")
                }

                is NetworkResult.Failure -> {
                    println(result502.error.userDescription())
                }
            }
            println("===========================")
        }
    }
}
