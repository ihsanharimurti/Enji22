package com.example.enjiapp.ui


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.enjiapp.databinding.ActivityQuotesBinding
import java.util.*
import kotlin.collections.ArrayList

class QuotesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuotesBinding
    private lateinit var affirmations: ArrayList<String>
    private var currentIndex = 0
    private val random = Random()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityQuotesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MainActivity.ScreenUtil.setupFullScreen(this)

        // Initialize affirmations list
        initializeAffirmations()

        // Set initial affirmation
        displayAffirmation(currentIndex)

        // Set click listeners
        binding.prevButton.setOnClickListener {
            showPreviousAffirmation()
        }

        binding.nextButton.setOnClickListener {
            showNextAffirmation()
        }

        binding.randomButton.setOnClickListener {
            showRandomAffirmation()
        }
    }

    private fun initializeAffirmations() {
        affirmations = arrayListOf(
            "I love you.",
            "You are enough.",
            "You are more than worthy.",
            "You shine in your own way.",
            "You matter.",
            "You are loved deeply.",
            "You complete the moment.",
            "You are a gift.",
            "You’re everything I prayed for.",
            "You are not missing anything.",
            "You are perfectly you.",
            "You’re my favorite person.",
            "You are strong and soft — beautifully.",
            "You’re doing amazing.",
            "You’re always enough.",
            "You don’t have to try hard — you already glow.",
            "You’re my peace.",
            "You’re more than beautiful.",
            "You’re my safe place.",
            "You are whole.",
            "You are seen.",
            "You’re not too much or too little.",
            "You’re just right.",
            "You’re my sunshine.",
            "You are appreciated.",
            "You’re a blessing.",
            "You make life sweeter.",
            "You are worthy of love.",
            "You make me proud.",
            "You’re my calm in chaos.",
            "You’re full of light.",
            "You are irreplaceable.",
            "You are exactly who you should be.",
            "You’re kind and powerful.",
            "You make me feel lucky.",
            "You’re my favorite hello.",
            "You are amazing, always.",
            "You are my heart.",
            "You inspire me.",
            "You are deeply enough.",
            "You are never lacking.",
            "You are love itself.",
            "You’re the reason I smile.",
            "You’re wonderfully made.",
            "You’re endlessly lovable.",
            "You’re my favorite thought.",
            "You make my world better.",
            "You’re so valued.",
            "You are magic.",
            "You’re everything good.",
            "You are brave.",
            "You’re the peace I never knew I needed.",
            "You are deserving of everything good.",
            "You are gold.",
            "You’re the light in my days.",
            "You’re never behind.",
            "You are perfect to me.",
            "You are a masterpiece.",
            "You are lovable, just as you are.",
            "You’re a dream come true.",
            "You’re the warmth in my life.",
            "You are more than enough.",
            "You are safe with me.",
            "You are seen and held.",
            "You’re my favorite story.",
            "You’re not missing a thing.",
            "You’re the calm in my storm.",
            "You’re a wonder.",
            "You are chosen.",
            "You are precious.",
            "You are admired.",
            "You are held in love.",
            "You’re more than your thoughts.",
            "You’re a light for others.",
            "You’re worthy of rest.",
            "You are joy.",
            "You’re never too little.",
            "You are adored.",
            "You’re someone to be celebrated.",
            "You are love, in its purest form."
        )

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun displayAffirmation(index: Int) {
        binding.affirmationTextView.text = affirmations[index]
    }

    private fun showNextAffirmation() {
        currentIndex = (currentIndex + 1) % affirmations.size
        displayAffirmation(currentIndex)
    }

    private fun showPreviousAffirmation() {
        currentIndex = (currentIndex - 1 + affirmations.size) % affirmations.size
        displayAffirmation(currentIndex)
    }

    private fun showRandomAffirmation() {
        var randomIndex: Int
        do {
            randomIndex = random.nextInt(affirmations.size)
        } while (randomIndex == currentIndex && affirmations.size > 1)

        currentIndex = randomIndex
        displayAffirmation(currentIndex)
    }
}