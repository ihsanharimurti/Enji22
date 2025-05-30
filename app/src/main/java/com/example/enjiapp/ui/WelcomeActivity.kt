package com.example.enjiapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.enjiapp.R
import com.example.enjiapp.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    private val messages = listOf(
        "Hi sayang",
        "Happy\nbirthday!",
        "I have something\nfor you",
        "Hope you \nliked it!"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MainActivity.ScreenUtil.setupFullScreen(this)
        showMessagesSequentially()
    }

    private fun showMessagesSequentially() {
        var index = 0
        val handler = Handler(Looper.getMainLooper())
        val delayMillis = 2000L

        fun showNext() {
            if (index < messages.size) {
                binding.welcomeText.text = messages[index]
                binding.welcomeText.alpha = 0f
                binding.welcomeText.animate().alpha(1f).setDuration(800).start()

                index++
                handler.postDelayed({ showNext() }, delayMillis)
            } else {
                val button = Button(this).apply {
                    text = "next"
                    textSize = 18f
                    isAllCaps = false
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                    setBackgroundResource(R.drawable.rounded_button)
                    setPadding(40, 20, 40, 20)

                    val layoutParams = ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topToBottom = binding.welcomeText.id
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                        topMargin = 160
                    }
                    this.layoutParams = layoutParams

                    setOnClickListener {
                        startActivity(Intent(this@WelcomeActivity, MenuActivity::class.java))
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                    }
                }

                binding.welcomeLayout.addView(button)
            }
        }

        showNext()
    }
}

