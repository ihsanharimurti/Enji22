package com.example.enjiapp.ui

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.enjiapp.R
import com.example.enjiapp.databinding.ActivityMainBinding
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var countDownTimer: CountDownTimer? = null
    private val targetDate: Calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, 2025)
        set(Calendar.MONTH, Calendar.APRIL)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        ScreenUtil.setupFullScreen(this)
        setupButtonAppearance()

        // Set up button click listener
        binding.btnNext.setOnClickListener {
            intentOne()
        }

        // Disable button initially and update its appearance
        setButtonEnabled(false)

        // Check if target date has already passed
        val currentTime = Calendar.getInstance().timeInMillis
        val targetTime = targetDate.timeInMillis
        if (targetTime <= currentTime) {
            setButtonEnabled(true)
        }

        // Start the countdown
        startCountdown()
    }

    private fun setupButtonAppearance() {
        // Ensure text color stays white regardless of button state
        binding.btnNext.setTextColor(Color.WHITE)
    }

    private fun setButtonEnabled(enabled: Boolean) {
        binding.btnNext.isEnabled = enabled

        // Change button background color based on state
        if (enabled) {
            // Use the default button color when enabled
            // You can replace this with your app's primary color if needed
            binding.btnNext.backgroundTintList =    ColorStateList.valueOf(ContextCompat.getColor(this,
                R.color.pink_main
            ))
        } else {
            // Set grey color when disabled
            binding.btnNext.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
        }

        // Ensure text remains white in both states
        binding.btnNext.setTextColor(Color.WHITE)
    }

    object ScreenUtil {
        fun setupFullScreen(activity: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                activity.window.setDecorFitsSystemWindows(false)
                activity.window.insetsController?.let {
                    it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                    it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                @Suppress("DEPRECATION")
                activity.window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                or View.SYSTEM_UI_FLAG_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        )
            }
        }
    }


    private fun startCountdown() {
        val currentTime = Calendar.getInstance().timeInMillis
        val targetTime = targetDate.timeInMillis

        if (targetTime <= currentTime) {
            // Target date has passed
            displayCountdownFinished()
            return
        }

        // Make sure button is disabled when countdown starts
        setButtonEnabled(false)

        val timeDifference = targetTime - currentTime

        countDownTimer = object : CountDownTimer(timeDifference, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                updateCountdownDisplay(millisUntilFinished)
            }

            override fun onFinish() {
                displayCountdownFinished()
                // Enable button when countdown finishes
                setButtonEnabled(true)
            }
        }.start()
    }

    private fun updateCountdownDisplay(millisUntilFinished: Long) {
        val days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
        val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60

        binding.tvDays.text = days.toString()
        binding.tvHours.text = String.format(Locale.getDefault(), "%02d", hours)
        binding.tvMinutes.text = String.format(Locale.getDefault(), "%02d", minutes)
        binding.tvSeconds.text = String.format(Locale.getDefault(), "%02d", seconds)
    }

    private fun displayCountdownFinished() {
        binding.tvDays.text = "00"
        binding.tvHours.text = "00"
        binding.tvMinutes.text = "00"
        binding.tvSeconds.text = "00"

        // Enable button when countdown is finished
        setButtonEnabled(true)
    }

    private fun intentOne() {
        startActivity(Intent(this, WelcomeActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up timer to prevent memory leaks
        countDownTimer?.cancel()
    }
}