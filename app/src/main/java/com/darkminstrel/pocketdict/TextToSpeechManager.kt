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
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
    private val liveDataUttering = MutableLiveData<Boolean>().apply { value = false }
    fun getLiveDataUttering() = liveDataUttering as LiveData<Boolean>

    private val initListener:TextToSpeech.OnInitListener = TextToSpeech.OnInitListener {status ->
        if(status == TextToSpeech.SUCCESS){
            tts.setOnUtteranceProgressListener(object: UtteranceProgressListener() {
                override fun onDone(utteranceId: String?) {
                    liveDataUttering.postValue(false)
                }
                override fun onError(utteranceId: String?) {
                    liveDataUttering.postValue(false)
                }
                override fun onStart(utteranceId: String?) {
                    liveDataUttering.postValue(true)
                }
            })
        }
    }
    private val tts:TextToSpeech = TextToSpeech(context, initListener)

    fun speak(what:String, lang:String){
        val params = Bundle().apply { putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, what) }
        tts.language = Locale(lang)
        tts.speak(what, TextToSpeech.QUEUE_FLUSH, params, what)
    }

    fun isMuted() = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) == 0
}