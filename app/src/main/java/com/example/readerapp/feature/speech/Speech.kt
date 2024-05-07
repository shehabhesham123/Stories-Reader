package com.example.readerapp.feature.speech

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.readerapp.core.network.NetworkHandler
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * we run speech in background thread with kotlin coroutines
 */
@OptIn(DelicateCoroutinesApi::class)
class Speech(private val context: Context) : TextToSpeech.OnInitListener {
    private var textToSpeech: TextToSpeech? = null

    var isSpeaking = false
        private set

    init {
        textToSpeech = TextToSpeech(context, this)
    }

    fun setOnUtteranceProgressListener(listener: UtteranceProgressListener) {
        textToSpeech!!.setOnUtteranceProgressListener(listener)
    }

    fun startSpeech(test: String) {
        isSpeaking = true
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "uniqueId")
        GlobalScope.launch(Dispatchers.Default) {
            textToSpeech!!.speak(
                test, TextToSpeech.QUEUE_FLUSH, params, "uniqueId"
            )
        }
    }

    fun stopSpeech() {
        textToSpeech?.apply {
            stop()
            this@Speech.isSpeaking = false
        }
    }

    fun shutdownSpeech() {
        textToSpeech?.shutdown()
    }

    override fun onInit(status: Int) {
        GlobalScope.launch(Dispatchers.Default) {
            if (NetworkHandler.isNetworkAvailable(context)) {
                if (status == TextToSpeech.SUCCESS) {
                    val output = textToSpeech!!.setLanguage(Locale.forLanguageTag("ar"))
                    if (output == TextToSpeech.LANG_MISSING_DATA || output == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported")
                    }
                } else {
                    Log.e("TTS", "Initialization failed")
                }
            } else {
                Log.e("TTS", "No internet")
            }
        }
    }

}