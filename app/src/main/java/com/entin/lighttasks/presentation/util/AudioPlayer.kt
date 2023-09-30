package com.entin.lighttasks.presentation.util

import android.app.Application
import android.media.MediaPlayer
import androidx.core.net.toUri
import java.io.File
import javax.inject.Inject

class AudioPlayer @Inject constructor(
    private val application: Application,
) {

    private var player: MediaPlayer? = null
    private var isPlaying: Boolean = false
    private  var isPause: Boolean = false

    /**
     * Listen to audio file
     */
    fun play(name: String) {
        if(isPause) {
            player?.start()
        } else {
            MediaPlayer.create(
                application.applicationContext, getFile(name).toUri()
            ).apply {
                player = this
                this@AudioPlayer.isPlaying = true
                start()
            }
        }
    }

    fun pause() {
        player?.pause()
        isPause = true
    }

    fun stop() {
        player?.stop()
        player?.release()
        player = null
        isPlaying = false
    }

    /**
     * Get photo file procedure
     */
    private fun getFile(imageName: String): File = File(getVoiceFileDir(), imageName)

    /**
     * Default application internal storage folder
     */
    private fun getVoiceFileDir() = application.applicationContext.filesDir
}
