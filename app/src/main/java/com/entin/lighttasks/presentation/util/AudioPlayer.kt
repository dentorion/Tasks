package com.entin.lighttasks.presentation.util

import android.app.Application
import android.media.MediaPlayer
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class AudioPlayer @Inject constructor(
    private val application: Application,
) {
    private var player: MediaPlayer? = null
    private var _playState = MutableLiveData(PlayState())
    val playState: LiveData<PlayState> get() = _playState
    private var job: Job? = null

    /**
     * Listen to audio file
     */
    fun play(name: String) {
        if(_playState.value?.isPause == true) {
            player?.start()
        } else if(_playState.value?.isPlaying == false) {
            MediaPlayer.create(application.applicationContext, getFile(name).toUri()).apply {
                player = this
                start()
            }
        }
        setTimer(isRunning = true)
        _playState.value = _playState.value?.copy(isPlaying = true, isPause = false, isStop = false)
        player?.setOnCompletionListener {
            stop()
        }
    }

    fun pause() {
        if(_playState.value?.isPlaying == true) {
            player?.pause()
            setTimer(isRunning = false)
            _playState.value = _playState.value?.copy(isPlaying = false, isPause = true, isStop = false)
        }
    }

    fun stop() {
        if(_playState.value?.isPlaying == true || _playState.value?.isPause == true) {
            player?.stop()
            setTimer(isRunning = false)
            player?.reset()
            player?.release()
            _playState.value = _playState.value?.copy(isPlaying = false, isPause = false, isStop = true, progress = ZERO)
        }
    }

    fun clear() {
        if(_playState.value?.isPlaying == true) {
            player?.stop()
            player?.reset()
            player?.release()
        }
        player = null
        job?.cancel()
        job = null
    }

    /** Set timer */
    private fun setTimer(isRunning: Boolean) {
        if (isRunning) {
            job?.cancel()
            job = CoroutineScope(Dispatchers.IO).launch {
                while (true) {
                    delay(ONE_SEC)
                    val currentPosition = player?.currentPosition ?: ZERO
                    val totalDuration = player?.duration ?: ZERO

                    if (totalDuration > ZERO) {
                        val progress = (currentPosition.toFloat() / totalDuration * 100).toInt()
                        withContext(Dispatchers.Main) {
                            _playState.value = _playState.value?.copy(progress = progress)
                        }
                    }
                }
            }
        } else {
            job?.cancel()
        }
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

        data class PlayState(
            val isPlaying: Boolean = false,
            val isPause: Boolean = false,
            val isStop: Boolean = false,
            val progress: Int = ZERO,
        )
    }
}
