package com.example.enjiapp.ui


import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.enjiapp.databinding.ActivityLetterBinding

class LetterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLetterBinding
    private var currentPage = 0
    private val totalPages = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLetterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPageContent()
        setupButtons()
    }

    private fun setupPageContent() {
        val birthdayMessages = listOf(
            "Happy Birthday, Sayang!\nOn your special day, I just want you to know that you deserve all the happiness in the world. I hope your day is filled with smiles, love, and sweet memories you'll always remember.",
            "Wishing you an amazing year ahead — full of new adventures, good things coming your way, and success in everything you do. I hope all your dreams slowly turn into reality."
        )

        binding.pageContent.text = birthdayMessages[currentPage]
        updatePageNumber()
        updateButtonVisibility()
    }

    private fun setupButtons() {
        binding.nextButton.setOnClickListener {
            if (currentPage < totalPages - 1) {
                currentPage++
                setupPageContent()
            }
        }

        binding.prevButton.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                setupPageContent()
            }
        }
    }

    private fun updatePageNumber() {
        binding.pageNumber.text = "Page ${currentPage + 1}/$totalPages"
    }

    private fun updateButtonVisibility() {
        binding.prevButton.visibility = if (currentPage > 0) View.VISIBLE else View.INVISIBLE
        binding.nextButton.visibility = if (currentPage < totalPages - 1) View.VISIBLE else View.INVISIBLE
    }
}