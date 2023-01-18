package com.darkminstrel.pocketdict.ui.main

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkminstrel.pocketdict.*
import com.darkminstrel.pocketdict.models.ParsedTranslation
import com.darkminstrel.pocketdict.usecases.UsecaseTranslate
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Immutable
sealed interface ViewState {
    object History : ViewState
    object Loading : ViewState
    data class Error(val error: Throwable) : ViewState
    data class Data(val parsed: ParsedTranslation, val context: String?) : ViewState
}

@Immutable
data class PropsMain(
    val query: TextFieldValue = TextFieldValue(),
    val onQueryChange: (TextFieldValue) -> Unit = {},
    val state: ViewState = ViewState.History,
    val onChosenContext: (String?) -> Unit = {},
    val speechState: SpeechState = SpeechState.IDLE,
    val onSpeak: (String) -> Unit = {},
    val history: History = History(),
    val onAddToHistory: (key: String, value: String) -> Unit = { _, _ -> },
    val onDeleteFromHistory: (String) -> Unit = {},
)

class ViewModelMain(
    private val usecase: UsecaseTranslate,
    val tts: TextToSpeechManager,
) : ViewModel() {

    private val _props = mutableStateOf(
        PropsMain(
            onQueryChange = this::onQueryChange,
            onSpeak = { what -> tts.speak(what = what, lang = "en") },
            onChosenContext = this::onChosenContext,
            onAddToHistory = this::onAddToHistory,
            onDeleteFromHistory = this::onDeleteFromHistory,
        )
    )
    val props = _props as State<PropsMain>
    private var job: Job? = null

    private fun onChosenContext(context: String?) {
        val state = props.value.state
        if (state is ViewState.Data) {
            _props.value = _props.value.copy(
                state = state.copy(
                    context = if (state.context == context) null else context
                )
            )
        }
    }

    init {
        viewModelScope.launch {
            tts.speechState.collect {
                _props.value = _props.value.copy(speechState = it)
            }
        }
        viewModelScope.launch {
            usecase.flowHistory.collect {
                _props.value = _props.value.copy(history = it)
            }
        }
    }

    private fun setState(state: ViewState) {
        _props.value = _props.value.copy(state = state)
    }

    private fun onQueryChange(query: TextFieldValue) {
        val oldText = _props.value.query.text.lowercase().trim()
        val text = query.text.lowercase().trim()
        _props.value = _props.value.copy(query = query)
        if (text != oldText) {
            job?.cancel()
            if (text.isEmpty()) setState(ViewState.History)
            else {
                setState(ViewState.Loading)
                job = viewModelScope.launch {
                    delay(200L)
                    usecase.getTranslation(text).fold(
                        onSuccess = { setState(ViewState.Data(it, it.defaultContext)) },
                        onFailure = { setState(ViewState.Error(it)) }
                    )
                }
            }
        }
    }

    private fun onAddToHistory(key: String, value: String) {
        DBG("onAddToHistory $key")
        viewModelScope.launch {
            usecase.addToHistory(key, value)
        }
    }

    private fun onDeleteFromHistory(key: String) {
        viewModelScope.launch {
            usecase.deleteFromHistory(key)
        }
    }

}