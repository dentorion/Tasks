package com.entin.lighttasks.presentation.util

import android.app.Application
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File
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

    /**
     * Init recorder
     */
    private fun init() {
        mediaRecorder = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(application)
        } else {
            @Suppress("deprecation")
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(16 * 44100)
            setAudioSamplingRate(96000)
            setOutputFile(generateName())
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
        init()
        mediaRecorder?.start()
    }

    /**
     * Stop recording
     */
    fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        clear()
    }

    /**
     * Delete audio file
     */
    fun delete(name: String) {
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

    /**
     * Clear MediaRecorder
     */
    private fun clear() {
        mediaRecorder = null
    }

    /**
     * Image name
     */
    private fun generateName() = "$AUDIO_PREFIX${
        SimpleDateFormat(DATE_FORMAT_NAME, Locale.US).format(System.currentTimeMillis())
    }$FORMAT_MP3"

    /**
     * Get photo file procedure
     */
    private fun getFile(imageName: String): File = File(getImageFileDir(), imageName)

    /**
     * Default application internal storage folder
     */
    private fun getImageFileDir(): File = application.applicationContext.filesDir
}
