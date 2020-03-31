package com.darkminstrel.pocketdict

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

class TextToSpeechManager(context: Context) {
    enum class SpeechState {IDLE, LOADING, UTTERING}

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
    private val liveDataUttering = MutableLiveData<SpeechState>().apply { value = SpeechState.IDLE }
    fun getLiveDataUttering() = liveDataUttering as LiveData<SpeechState>

    private val initListener:TextToSpeech.OnInitListener = TextToSpeech.OnInitListener {status ->
        if(status == TextToSpeech.SUCCESS){
            tts.setOnUtteranceProgressListener(object: UtteranceProgressListener() {
                override fun onDone(utteranceId: String?) {
                    liveDataUttering.postValue(SpeechState.IDLE)
                }
                override fun onError(utteranceId: String?) {
                    liveDataUttering.postValue(SpeechState.IDLE)
                }
                override fun onStart(utteranceId: String?) {
                    liveDataUttering.postValue(SpeechState.UTTERING)
                }
            })
        }
    }
    private val tts:TextToSpeech = TextToSpeech(context, initListener)

    fun speak(what:String, lang:String){
        liveDataUttering.postValue(SpeechState.LOADING)
        val params = Bundle().apply { putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, what) }
        tts.language = Locale(lang)
        tts.speak(what, TextToSpeech.QUEUE_FLUSH, params, what)
    }

    fun isMuted() = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) == 0
}