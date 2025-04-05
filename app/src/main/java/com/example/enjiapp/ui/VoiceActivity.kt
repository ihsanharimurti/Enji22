package com.example.enjiapp.ui

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.example.enjiapp.R
import com.example.enjiapp.databinding.ActivityVoiceBinding
import com.example.enjiapp.services.MusicService

class VoiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVoiceBinding
    private var musicService: MusicService? = null
    private var isBound = false
    private val audioResId = R.raw.safe_sounds // Ganti dengan ID file audio Anda

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            isBound = true
            updateUI()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            isBound = false
        }
    }

    private val playbackReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val isPlaying = intent?.getBooleanExtra("playing", false) ?: false
            val isPaused = intent?.getBooleanExtra("paused", false) ?: false
            updatePlaybackStatus(isPlaying, isPaused)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityVoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MainActivity.ScreenUtil.setupFullScreen(this)

        // Bind to the music service
        val intent = Intent(this, MusicService::class.java)
        startService(intent)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        MainActivity.ScreenUtil.setupFullScreen(this)
        // Register broadcast receiver
        registerReceiver(playbackReceiver, IntentFilter(MusicService.PLAYBACK_STATUS_CHANGED))
        // Set up click listeners
        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        binding.playButton.setOnClickListener {
            if (musicService?.isPlaying() == false && musicService?.isPaused() == false) {
                musicService?.playLocalMusic(audioResId)
            } else {
                musicService?.restartMusic()
            }
        }

        binding.pauseResumeButton.setOnClickListener {
            if (musicService?.isPlaying() == true) {
                musicService?.pauseMusic()
            } else if (musicService?.isPaused() == true) {
                musicService?.resumeMusic()
            }
        }

        binding.stopButton.setOnClickListener {
            musicService?.stopMusic()
        }


    }

    private fun updatePlaybackStatus(isPlaying: Boolean, isPaused: Boolean) {
        when {
            isPlaying -> {
                binding.playButton.setImageResource(R.drawable.ic_restart_voice)
                binding.pauseResumeButton.setImageResource(R.drawable.ic_pause_voice)
                binding.pauseResumeButton.isEnabled = true
                binding.stopButton.isEnabled = true
            }
            isPaused -> {
                binding.playButton.setImageResource(R.drawable.ic_restart_voice)
                binding.pauseResumeButton.setImageResource(R.drawable.ic_play_voice)
                binding.pauseResumeButton.isEnabled = true
                binding.stopButton.isEnabled = true
            }
            else -> {
                binding.playButton.setImageResource(R.drawable.ic_play_voice)
                binding.pauseResumeButton.setImageResource(R.drawable.ic_pause_voice)
                binding.pauseResumeButton.isEnabled = false
                binding.stopButton.isEnabled = false
            }
        }
    }

//    private fun setupButtonListeners() {
//        binding.playButton.setOnClickListener {
//            // Play local audio or restart if it was stopped
//            if (musicService?.isPlaying() == false && musicService?.isPaused() == false) {
//                musicService?.playLocalMusic(audioResId)
//            } else {
//                musicService?.restartMusic()
//            }
//        }
//
//        binding.pauseResumeButton.setOnClickListener {
//            // Toggle between pause and resume based on current state
//            if (musicService?.isPlaying() == true) {
//                musicService?.pauseMusic()
//            } else if (musicService?.isPaused() == true) {
//                musicService?.resumeMusic()
//            }
//        }
//
//        binding.stopButton.setOnClickListener {
//            musicService?.stopMusic()
//        }
//
//        binding.backButton.setOnClickListener {
//            finish()
//        }
//    }

    private fun updateUI() {
        val isPlaying = musicService?.isPlaying() ?: false
        val isPaused = musicService?.isPaused() ?: false
        updatePlaybackStatus(isPlaying, isPaused)
    }

//    private fun updatePlaybackStatus(isPlaying: Boolean, isPaused: Boolean) {
//        when {
//            isPlaying -> {
//                binding.statusTextView.text = "Status: Playing"
//                binding.playButton.isEnabled = true  // Enable restart functionality
//                binding.pauseResumeButton.isEnabled = true
//                binding.pauseResumeButton.text = "Pause"
//                binding.stopButton.isEnabled = true
//            }
//            isPaused -> {
//                binding.statusTextView.text = "Status: Paused"
//                binding.playButton.isEnabled = true  // Allow restart even when paused
//                binding.pauseResumeButton.isEnabled = true
//                binding.pauseResumeButton.text = "Resume"
//                binding.stopButton.isEnabled = true
//            }
//            else -> {
//                binding.statusTextView.text = "Status: Stopped"
//                binding.playButton.isEnabled = true
//                binding.pauseResumeButton.isEnabled = false
//                binding.pauseResumeButton.text = "Pause"  // Reset text
//                binding.stopButton.isEnabled = false
//            }
//        }
//    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onPause() {
        super.onPause()
        // When leaving this activity, stop the audio if it's playing in local mode
        musicService?.stopIfLocalPlayback()
    }

    override fun onDestroy() {
        unregisterReceiver(playbackReceiver)
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
        super.onDestroy()
    }
}