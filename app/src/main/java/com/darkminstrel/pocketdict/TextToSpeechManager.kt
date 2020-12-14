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
    private val _liveDataUttering = MutableLiveData<SpeechState>().apply { value = SpeechState.IDLE }
    val liveDataUttering = _liveDataUttering as LiveData<SpeechState>

    private val initListener:TextToSpeech.OnInitListener = TextToSpeech.OnInitListener {status ->
        if(status == TextToSpeech.SUCCESS){
            tts.setOnUtteranceProgressListener(object: UtteranceProgressListener() {
                override fun onDone(utteranceId: String?) {
                    _liveDataUttering.postValue(SpeechState.IDLE)
                }
                override fun onError(utteranceId: String?) {
                    _liveDataUttering.postValue(SpeechState.IDLE)
                }
                override fun onStart(utteranceId: String?) {
                    _liveDataUttering.postValue(SpeechState.UTTERING)
                }
            })
        }
    }
    private val tts:TextToSpeech = TextToSpeech(context, initListener)

    fun speak(what:String, lang:String):Boolean{
        val params = Bundle().apply { putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, what) }
        tts.language = Locale(lang)
        val result = tts.speak(what, TextToSpeech.QUEUE_FLUSH, params, what)
        return when(result){
            TextToSpeech.SUCCESS ->{
                _liveDataUttering.postValue(SpeechState.LOADING)
                true
            }
            else ->{
                _liveDataUttering.postValue(SpeechState.IDLE)
                false
            }
        }
    }

    fun isMuted() = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) == 0
}