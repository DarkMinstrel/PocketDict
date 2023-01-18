package com.darkminstrel.pocketdict

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

@Immutable
enum class SpeechState { IDLE, LOADING, UTTERING }

class TextToSpeechManager(context: Context) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
    private val _speechState = MutableStateFlow(SpeechState.IDLE)
    val speechState = _speechState as Flow<SpeechState>

    private val initListener: TextToSpeech.OnInitListener = TextToSpeech.OnInitListener { status ->
        if (status == TextToSpeech.SUCCESS) {
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onDone(utteranceId: String?) {
                    _speechState.value = SpeechState.IDLE
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _speechState.value = SpeechState.IDLE
                }

                override fun onStart(utteranceId: String?) {
                    _speechState.value = SpeechState.UTTERING
                }
            })
        }
    }
    private val tts: TextToSpeech = TextToSpeech(context, initListener)

    fun speak(what: String, lang: String): Boolean {
        val params = Bundle().apply { putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, what) }
        tts.language = Locale(lang)
        val result = tts.speak(what, TextToSpeech.QUEUE_FLUSH, params, what)
        return when (result) {
            TextToSpeech.SUCCESS -> {
                _speechState.value = SpeechState.LOADING
                true
            }
            else -> {
                _speechState.value = SpeechState.IDLE
                false
            }
        }
    }

    fun isMuted() = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) == 0
}