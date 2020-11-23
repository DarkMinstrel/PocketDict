package com.darkminstrel.pocketdict.ui.act_main

import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import com.darkminstrel.pocketdict.Config
import com.darkminstrel.pocketdict.R
import com.darkminstrel.pocketdict.TextToSpeechManager
import com.darkminstrel.pocketdict.data.ViewStateTranslate
import com.darkminstrel.pocketdict.utils.DBG
import com.darkminstrel.pocketdict.utils.getDrawableFromAttribute
import com.darkminstrel.pocketdict.utils.nullOrNotEmpty
import com.darkminstrel.pocketdict.utils.trimQuery
import kotlinx.coroutines.CoroutineScope

class ActMainView(scope: CoroutineScope, rootView: View, vm: ActMainVM) {
    private val imm = rootView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    private val searchView = rootView.findViewById<SearchView>(R.id.searchView)
    private val searchViewEditText: AutoCompleteTextView? = searchView.findViewById<AutoCompleteTextView>(R.id.search_src_text)?.apply{
        threshold = 0
    }
    private val containerInput:View = rootView.findViewById(R.id.containerInput)

    private val toolbar = rootView.findViewById<Toolbar>(R.id.toolbar).apply{
        setNavigationOnClickListener { vm.tryReset() }
    }
    private val viewList = ActMainViewList(scope, rootView, vm, this::clearFocus)
    private val viewDetails = ActMainViewDetails(rootView, vm)

    init {
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String):Boolean {
                viewList.onQueryChanged(trimQuery(newText))
                return false
            }
            override fun onQueryTextSubmit(query: String): Boolean {
                viewList.onQueryChanged("") //remove search results
                vm.onQuerySubmit(trimQuery(query))
                return true
            }
        })
        searchViewEditText?.setOnFocusChangeListener { v, hasFocus ->
            DBG("searchViewEditText focus = $hasFocus")
            if(hasFocus) {
                imm?.showSoftInput(v, 0)
                //vm.tryReset()
            }
        }
        if(Config.ENABLE_IME_INSETS && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            setupInsets(rootView)
        }
    }

    fun setViewState(viewState: ViewStateTranslate?) {
        DBG("View state: ${viewState?.javaClass?.simpleName}")
        modifyToolbar(viewState)

        if(viewState!=null){
            viewDetails.setViewState(viewState)
            viewDetails.setVisible(true)
            viewList.setVisible(false)
            clearFocus()
        }else{
            viewList.setVisible(true)
            viewDetails.setVisible(false)
            requestFocus()
        }
    }

    private fun modifyToolbar(viewState: ViewStateTranslate?){
        toolbar.title = when(viewState){
            is ViewStateTranslate.Data -> viewState.translation.source
            is ViewStateTranslate.Progress -> ""
            is ViewStateTranslate.Error -> viewState.query
            else -> toolbar.resources.getString(R.string.appName)
        }
        toolbar.subtitle = when(viewState){
            is ViewStateTranslate.Data -> {
                val transcription = viewState.translation.transcription.nullOrNotEmpty()
                transcription?.let{"[$it]"}
            }
            else -> null
        }
        toolbar.navigationIcon = if(viewState!=null) toolbar.context.getDrawableFromAttribute(R.attr.homeAsUpIndicator) else null
    }

    fun setSpeechState(speechState: TextToSpeechManager.SpeechState) = viewDetails.setSpeechState(speechState)

    fun requestFocus() {
        DBG("Requesting focus")
        searchViewEditText?.selectAll()
        searchView.requestFocus()
    }

    private fun clearFocus() {
        DBG("Clearing focus")
        searchView.clearFocus()
    }

    fun setKeys(keys:List<String>) {
        viewList.setKeys(keys)
        viewDetails.setKeys(keys)
    }

    fun tryClearInput():Boolean {
        return if(searchView.query.isNotEmpty()){
            searchView.setQuery("", false)
            requestFocus()
            true
        }else false
    }

    @RequiresApi(30)
    private fun setupInsets(rootView:View){
        rootView.doOnLayout {
            rootView.rootWindowInsets?.let { insets -> applyInsets(insets) }
            rootView.setWindowInsetsAnimationCallback(object : WindowInsetsAnimation.Callback(DISPATCH_MODE_STOP) {
                override fun onProgress(insets: WindowInsets, animations: MutableList<WindowInsetsAnimation>): WindowInsets {
                    applyInsets(insets)
                    return insets
                }
            })
            //TODO floating keyboard
        }
    }

    @RequiresApi(30)
    private fun applyInsets(insets: WindowInsets){
        val top = insets.getInsets(WindowInsets.Type.systemBars()).top
        val bottom = Integer.max(insets.getInsets(WindowInsets.Type.ime()).bottom, insets.getInsets(WindowInsets.Type.navigationBars()).bottom)
        toolbar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMargins(top = top)
        }
        containerInput.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMargins(bottom = bottom)
        }
    }


}

