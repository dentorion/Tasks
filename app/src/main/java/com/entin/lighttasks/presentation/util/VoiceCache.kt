package com.entin.lighttasks.presentation.util

import android.app.Application
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceCache @Inject constructor(
    private val application: Application,
) {
    private var mediaRecorder: MediaRecorder? = null
    private var _recordState = MutableLiveData(RecorderState())
    val recordState: LiveData<RecorderState> get() = _recordState
    private var fileName = EMPTY_STRING
    private var timer = ZERO_LONG
    private var job: Job? = null

    /**
     * Init recorder
     */
    private fun initMediaRecorder() {
        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(application)
        } else {
            @Suppress("deprecation") MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(BIT_RATE)
            setAudioSamplingRate(SAMPLE_RATE)
            setOutputFile(FileOutputStream(getFile(fileName)).fd)
            setMaxDuration(MAX_DURATION_1_SEC)
            try {
                prepare()
            } catch (e: IOException) {
                Log.e("Global", "Audio record. Prepare() failed.")
            }
        }
    }

    /**
     * Start recording
     */
    fun startRecording() {
        generateFileName()
        initMediaRecorder()
        setTimer(isRunning = true)
        mediaRecorder?.start()
        _recordState.value = RecorderState(
            isRecording = true, timer = secondsToTextValue(timer), fileName = fileName
        )
    }

    /**
     * Stop recording
     */
    fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            setTimer(isRunning = false)
            release()
        }
        _recordState.value = RecorderState(
            isRecording = false, timer = secondsToTextValue(timer), fileName = fileName
        )
    }

    /**
     * Delete audio file
     */
    fun delete(name: String) {
        fileName = EMPTY_STRING
        val fileToDelete = getFile(name)
        if (fileToDelete.exists()) {
            fileToDelete.delete()
        }
    }

    /**
     * Listen to audio file
     */
    fun play(name: String) {

    }

    fun pause() {

    }

    fun stop() {

    }

    /** Set timer */
    private fun setTimer(isRunning: Boolean) {
        if (isRunning) {
            job?.cancel()
            job = CoroutineScope(Dispatchers.IO).launch {
                while (true) {
                    delay(ONE_SEC)
                    timer += ONE
                    withContext(Dispatchers.Main) {
                        _recordState.value = _recordState.value?.copy(timer = secondsToTextValue(timer))
                    }
                }
            }
        } else {
            timer = ZERO_LONG
            job?.cancel()
        }
    }

    /**
     * Get timer as text
     */
    private fun secondsToTextValue(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        val minutesStr = String.format("%02d", minutes)
        val secondsStr = String.format("%02d", remainingSeconds)
        return "$minutesStr:$secondsStr"
    }

    /**
     * Clear MediaRecorder
     */
    fun clear() {
        mediaRecorder = null
        timer = ZERO_LONG
        fileName = EMPTY_STRING
        _recordState.value = RecorderState()
        job?.cancel()
    }

    /**
     * Image name
     */
    private fun generateFileName() {
        fileName = "$AUDIO_PREFIX${
            SimpleDateFormat(DATE_FORMAT_NAME, Locale.US).format(System.currentTimeMillis())
        }$FORMAT_MP3"
    }

    /**
     * Get photo file procedure
     */
    private fun getFile(imageName: String): File = File(getVoiceFileDir(), imageName)

    /**
     * Default application internal storage folder
     */
    private fun getVoiceFileDir() = application.applicationContext.filesDir

    companion object {
        private const val ONE_SEC = 1000L
        private const val SAMPLE_RATE = 96000
        private const val BIT_RATE = 16 * 44100
        private const val MAX_DURATION_1_SEC = 60000
        private const val MAX_DURATION_2_SEC = 120000
        private const val INITIAL_TIMER_VALUE = "00:00"

        data class RecorderState(
            val isRecording: Boolean = false,
            val timer: String = INITIAL_TIMER_VALUE,
            val fileName: String? = null,
        )
    }
}
