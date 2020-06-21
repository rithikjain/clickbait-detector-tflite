package com.rithikjain.clickbaittflite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.HapticFeedbackConstants
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var mJob: Job
    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mJob = Job()

        predictButton.isEnabled = false

        val classifier = Classifier(this)

        val meh = async {
            withContext(Dispatchers.IO) {
                classifier.init()
                withContext(Dispatchers.Main) {
                    predictButton.isEnabled = true
                }
            }
        }

        predictButton.isHapticFeedbackEnabled = true
        predictButton.setOnClickListener {
            predictButton.performHapticFeedback(
                HapticFeedbackConstants.VIRTUAL_KEY,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )

            if (inputText.text.isEmpty()) {
                Toast.makeText(this, "Text cant be empty", Toast.LENGTH_SHORT).show()
            } else {
                predictButton.isEnabled = false
                val text = inputText.text.toString().toLowerCase(Locale.ROOT).trim()
                val resArr = classifier.classifyText(text)
                predictButton.isEnabled = true
                if (resArr[0] > 0.5) {
                    resultText.text = "Looks like its clickbait !"
                } else {
                    resultText.text = "Nah not clickbait"
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mJob.cancel()
    }
}