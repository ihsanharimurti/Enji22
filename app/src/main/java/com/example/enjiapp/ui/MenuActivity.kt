package com.example.enjiapp.ui

import android.animation.ObjectAnimator
import com.example.enjiapp.databinding.ActivityMainBinding
import com.example.enjiapp.databinding.ActivityMenuBinding
import com.example.enjiapp.services.MusicService
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.enjiapp.R

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private var musicService: MusicService? = null
    private var isBound = false
    private var rotationAnimator: ObjectAnimator? = null


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
            updatePlaybackStatus(isPlaying)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Start and bind the music service
        val intent = Intent(this, MusicService::class.java)
        startService(intent)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        // Register broadcast receiver
        registerReceiver(playbackReceiver, IntentFilter(MusicService.PLAYBACK_STATUS_CHANGED))

        // Set up click listeners
        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        binding.playButton.setOnClickListener {
            musicService?.playGlobalMusic(R.raw.hbd_piano)
        }

        binding.stopButton.setOnClickListener {
            musicService?.stopMusic()
        }

        binding.goToGallery.setOnClickListener {
            startActivity(Intent(this, ImageShowActivity::class.java))
        }

        binding.goToQuote.setOnClickListener {
            startActivity(Intent(this, QuotesActivity::class.java))
        }

        binding.goToLetter.setOnClickListener {
            startActivity(Intent(this, LetterActivity::class.java))
        }

        binding.goToVn.setOnClickListener {
            musicService?.stopMusic()
            startActivity(Intent(this, VoiceActivity::class.java))
        }

    }

    private fun updateUI() {
        val isPlaying = musicService?.isPlaying() ?: false
        updatePlaybackStatus(isPlaying)
    }


    private fun updatePlaybackStatus(isPlaying: Boolean) {
        if (rotationAnimator == null) {
            rotationAnimator = ObjectAnimator.ofFloat(binding.rotatingCake, View.ROTATION, 0f, 360f).apply {
                duration = 4000
                repeatCount = ObjectAnimator.INFINITE
                interpolator = LinearInterpolator()
            }
        }

        if (isPlaying) {
            binding.rotatingCake.rotation = 0f // reset ke posisi awal
            rotationAnimator?.cancel() // cancel animasi sebelumnya
            rotationAnimator?.start()
        } else {
            rotationAnimator?.cancel() // stop animasi
        }
    }


    override fun onResume() {
        super.onResume()
        if (isBound) {
            updateUI()
        }
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