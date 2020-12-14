package com.darkminstrel.pocketdict.ui.act_main

import android.content.Context
import android.os.Build
import android.view.*
import android.view.WindowInsets.*
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
import com.darkminstrel.pocketdict.databinding.ActMainBinding
import com.darkminstrel.pocketdict.utils.DBG
import com.darkminstrel.pocketdict.utils.getDrawableFromAttribute
import com.darkminstrel.pocketdict.utils.nullOrNotEmpty
import com.darkminstrel.pocketdict.utils.trimQuery
import kotlinx.coroutines.CoroutineScope

class ActMainView(scope: CoroutineScope, private val binding:ActMainBinding, vm: ActMainVM) {
    private val imm = binding.root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    private val searchViewEditText: AutoCompleteTextView? = binding.searchView.findViewById<AutoCompleteTextView>(R.id.search_src_text)?.apply{
        threshold = 0
    }

    private val viewList = ActMainViewList(scope, binding, vm, this::clearFocus)
    private val viewDetails = ActMainViewDetails(binding, vm, this::clearFocus)

    init {
        binding.toolbar.apply {
            setNavigationOnClickListener {
                if(vm.tryReset()) requestFocus(true)
            }
        }
        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String):Boolean {
                viewList.onQueryChanged(trimQuery(newText))
                vm.onQueryChanged(trimQuery(newText))
                return false
            }
            override fun onQueryTextSubmit(query: String): Boolean {
                //viewList.onQueryChanged("") //remove search results
                clearFocus()
                vm.onQuerySubmit(query)
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
            setupInsets(binding.root)
        }
    }

    fun setViewState(viewState: ViewStateTranslate?) {
        DBG("View state: ${viewState?.javaClass?.simpleName}")
        modifyToolbar(viewState)

        viewDetails.setViewState(viewState)
        viewList.setVisible(viewState==null)
        if(viewState==null) requestFocus(false)
    }

    private fun modifyToolbar(viewState: ViewStateTranslate?){
        binding.toolbar.apply{
            title = when(viewState){
            is ViewStateTranslate.Data -> viewState.translation.source
            is ViewStateTranslate.Progress -> ""
            is ViewStateTranslate.Error -> viewState.query
            else -> resources.getString(R.string.appName)
        }
            subtitle = when(viewState){
                is ViewStateTranslate.Data -> {
                    val transcription = viewState.translation.transcription.nullOrNotEmpty()
                    transcription?.let{"[$it]"}
                }
                else -> null
            }
            navigationIcon = if(viewState!=null) context.getDrawableFromAttribute(R.attr.homeAsUpIndicator) else null
        }
    }

    fun setSpeechState(speechState: TextToSpeechManager.SpeechState) = viewDetails.setSpeechState(speechState)

    fun requestFocus(selectAll:Boolean) {
        DBG("Requesting focus")
        if(selectAll) searchViewEditText?.selectAll()
        binding.searchView.requestFocus()
    }

    private fun clearFocus() {
        DBG("Clearing focus")
        binding.searchView.clearFocus()
    }

    fun setKeys(keys:List<String>) {
        viewList.setKeys(keys)
        viewDetails.setKeys(keys)
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
        val all = insets.getInsets(Type.systemBars() or Type.ime())
        binding.constraintLayout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMargins(top = all.top, bottom = all.bottom, left = all.left, right = all.right)
        }
    }


}

