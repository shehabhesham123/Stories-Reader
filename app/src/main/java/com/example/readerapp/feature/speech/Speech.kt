package com.example.readerapp.feature.speech

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Toast
import com.example.readerapp.core.network.NetworkHandler
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(DelicateCoroutinesApi::class)
class Speech(private val context: Context) : TextToSpeech.OnInitListener {
    private var textToSpeech: TextToSpeech? = null

    init {
        textToSpeech = TextToSpeech(context, this)
    }

    fun setOnUtteranceProgressListener(listener: UtteranceProgressListener) {
        textToSpeech!!.setOnUtteranceProgressListener(listener)
    }

    fun startSpeech(test: String) {
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "uniqueId")
        GlobalScope.launch(Dispatchers.Default) {
            textToSpeech!!.speak(
                test,
                TextToSpeech.QUEUE_FLUSH,
                params,
                "uniqueId"
            )
        }
    }

    fun stopSpeech() {
        textToSpeech?.apply {
            stop()
            shutdown()
        }
    }

    override fun onInit(status: Int) {
        GlobalScope.launch(Dispatchers.Default) {
            if (NetworkHandler.isNetworkAvailable(context)) {
                if (status == TextToSpeech.SUCCESS) {
                    val output = textToSpeech!!.setLanguage(Locale.forLanguageTag("ar"))
                    if (output == TextToSpeech.LANG_MISSING_DATA || output == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            } else {
                Toast.makeText(
                    context,
                    "You must connect the internet",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}