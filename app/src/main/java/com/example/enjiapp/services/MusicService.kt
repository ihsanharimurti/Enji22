package com.example.enjiapp.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.example.enjiapp.R


class MusicService : Service() {

    // Media player instance
    private var mediaPlayer: MediaPlayer? = null
    private val binder = MusicBinder()

    // Playback state
    private var isPlaying = false
    private var isPaused = false

    // Track which mode is currently active
    private var isGlobalMode = false
    private var currentAudioResId = -1

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
    }

    // Play music that continues across activities
    fun playGlobalMusic(resId: Int) {
        // If we're in local mode or playing different audio, release current player
        if (!isGlobalMode || currentAudioResId != resId) {
            releaseMediaPlayer()
        }

        // If same global audio is paused, just resume it
        else if (isPaused && currentAudioResId == resId) {
            resumeMusic()
            return
        }

        // Create new MediaPlayer if needed
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, resId)
            mediaPlayer?.setOnCompletionListener {
                isPlaying = false
                isPaused = false
                sendBroadcast(Intent(PLAYBACK_STATUS_CHANGED)
                    .putExtra("playing", false)
                    .putExtra("paused", false))
            }
        }

        // Start playback
        mediaPlayer?.start()
        isPlaying = true
        isPaused = false
        isGlobalMode = true
        currentAudioResId = resId

        // Broadcast the play status change
        sendBroadcast(Intent(PLAYBACK_STATUS_CHANGED)
            .putExtra("playing", true)
            .putExtra("paused", false)
            .putExtra("globalMode", true))
    }

    // Play music that should only play in one activity
    fun playLocalMusic(resId: Int) {
        // ALWAYS force stop any previous playback regardless of mode
        releaseMediaPlayer()

        // Create new MediaPlayer with the specified resource
        mediaPlayer = MediaPlayer.create(this, resId)
        mediaPlayer?.setOnCompletionListener {
            isPlaying = false
            isPaused = false
            sendBroadcast(Intent(PLAYBACK_STATUS_CHANGED)
                .putExtra("playing", false)
                .putExtra("paused", false))
        }

        // Start playback
        mediaPlayer?.start()
        isPlaying = true
        isPaused = false
        isGlobalMode = false  // Important: set to local mode
        currentAudioResId = resId

        // Broadcast the play status change
        sendBroadcast(Intent(PLAYBACK_STATUS_CHANGED)
            .putExtra("playing", true)
            .putExtra("paused", false)
            .putExtra("globalMode", false))
    }

    // Pause the current playback
    fun pauseMusic() {
        if (mediaPlayer != null && isPlaying && !isPaused) {
            mediaPlayer?.pause()
            isPlaying = false
            isPaused = true

            // Broadcast the pause status
            sendBroadcast(Intent(PLAYBACK_STATUS_CHANGED)
                .putExtra("playing", false)
                .putExtra("paused", true)
                .putExtra("globalMode", isGlobalMode))
        }
    }

    // Resume paused playback
    fun resumeMusic() {
        if (mediaPlayer != null && isPaused) {
            mediaPlayer?.start()
            isPlaying = true
            isPaused = false

            // Broadcast the resumed status
            sendBroadcast(Intent(PLAYBACK_STATUS_CHANGED)
                .putExtra("playing", true)
                .putExtra("paused", false)
                .putExtra("globalMode", isGlobalMode))
        }
    }

    // Restart playback from beginning
    fun restartMusic() {
        if (mediaPlayer != null) {
            mediaPlayer?.seekTo(0)
            if (!isPlaying) {
                mediaPlayer?.start()
                isPlaying = true
                isPaused = false
            }

            // Broadcast the status
            sendBroadcast(Intent(PLAYBACK_STATUS_CHANGED)
                .putExtra("playing", true)
                .putExtra("paused", false)
                .putExtra("globalMode", isGlobalMode))
        }
    }

    // Stop music playback completely
    fun stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer?.pause()
            mediaPlayer?.seekTo(0)
            isPlaying = false
            isPaused = false

            // Broadcast the play status change
            sendBroadcast(Intent(PLAYBACK_STATUS_CHANGED)
                .putExtra("playing", false)
                .putExtra("paused", false)
                .putExtra("globalMode", isGlobalMode))
        }
    }

    // For activities to call when they're being destroyed
    fun stopIfLocalPlayback() {
        if (!isGlobalMode && (isPlaying || isPaused)) {
            stopMusic()
        }
    }

    fun isPlaying(): Boolean {
        return isPlaying
    }

    fun isPaused(): Boolean {
        return isPaused
    }

    fun isGlobalPlayback(): Boolean {
        return isGlobalMode
    }

    private fun releaseMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.stop()
                }
                mediaPlayer?.release()
            } catch (e: Exception) {
                // Handle potential errors
            }
            mediaPlayer = null
            isPlaying = false
            isPaused = false
        }
    }

    override fun onDestroy() {
        releaseMediaPlayer()
        super.onDestroy()
    }

    companion object {
        const val PLAYBACK_STATUS_CHANGED = "com.example.simplemusicplayer.PLAYBACK_STATUS_CHANGED"
    }
}
