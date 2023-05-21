package com.example.pilearner

import android.graphics.Typeface
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener { // Implement OnInitListener
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DynamicColors.applyToActivitiesIfAvailable(application)

        tts = TextToSpeech(applicationContext, this) // Set OnInitListener as "this"

        val piString = getString(R.string.pi)

        fun menu() {
            setContentView(R.layout.menu)

            val playButton = findViewById<Button>(R.id.Play)

            fun startGame(hard: Boolean) {
                setContentView(R.layout.game)

                var index = 0

                val buttons = mapOf(
                    '0' to findViewById<Button>(R.id.btnNumpad0) as Button,
                    '1' to findViewById<Button>(R.id.btnNumpad1) as Button,
                    '2' to findViewById<Button>(R.id.btnNumpad2) as Button,
                    '3' to findViewById<Button>(R.id.btnNumpad3) as Button,
                    '4' to findViewById<Button>(R.id.btnNumpad4) as Button,
                    '5' to findViewById<Button>(R.id.btnNumpad5) as Button,
                    '6' to findViewById<Button>(R.id.btnNumpad6) as Button,
                    '7' to findViewById<Button>(R.id.btnNumpad7) as Button,
                    '8' to findViewById<Button>(R.id.btnNumpad8) as Button,
                    '9' to findViewById<Button>(R.id.btnNumpad9) as Button
                )

                val answered = findViewById<android.widget.TextView>(R.id.EnteredPiLabel)
                answered.text = "3."

                val digitCount = findViewById<android.widget.TextView>(R.id.DigitCount)
                digitCount.text = (index + 1).toString()

                if (!hard) {
                    buttons[piString[index]]?.setTypeface(null, Typeface.BOLD_ITALIC)
                }


                for ((number, button) in buttons) {
                    button.setOnClickListener {
                        if (piString[index] != number) {
                            if (hard) {
                                setContentView(R.layout.menu)
                                menu()

                                index = 0
                            }
                        } else {
                            val newText = answered.text.takeLast(10).toString() + number.toString()
                            val spannableString = SpannableString(newText)
                            val textLength = newText.length.toFloat()
                            for (i in newText.indices) {
                                val proportion = (i + 1).toFloat() / textLength
                                spannableString.setSpan(RelativeSizeSpan(proportion), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            }

                            answered.text = spannableString
                            buttons[piString[index]]?.setTypeface(null, Typeface.NORMAL)
                            index += 1
                            digitCount.text = (index + 1).toString()
                            if (!hard) {
                                buttons[piString[index]]?.setTypeface(null, Typeface.BOLD_ITALIC)
                            }
                            tts?.speak(number.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
                        }
                    }
                }
            }

            playButton.setOnClickListener {
                startGame(true)
            }

            val learnButton = findViewById<Button>(R.id.Learn)

            learnButton.setOnClickListener {
                startGame(false)
            }
        }

        menu()
        window.insetsController?.let {
            it.hide(WindowInsets.Type.navigationBars())
            it.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // release the TextToSpeech resources
        tts?.stop()
        tts?.shutdown()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.getDefault())

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // not supported
            } else {
                tts?.setSpeechRate(5f)
            }
        }
    }
}