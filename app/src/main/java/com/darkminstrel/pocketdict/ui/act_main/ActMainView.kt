package com.darkminstrel.pocketdict.ui.act_main

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.darkminstrel.pocketdict.R
import com.darkminstrel.pocketdict.TextToSpeechManager
import com.darkminstrel.pocketdict.data.ViewStateTranslate
import com.darkminstrel.pocketdict.utils.DBG
import com.darkminstrel.pocketdict.utils.getDrawableFromAttribute
import com.darkminstrel.pocketdict.utils.trimQuery
import kotlinx.coroutines.CoroutineScope

class ActMainView(scope: CoroutineScope, rootView: View, vm: ActMainVM) {
    private val imm = rootView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    private val searchView = rootView.findViewById<SearchView>(R.id.searchView)
    private val searchViewEditText: AutoCompleteTextView? = searchView.findViewById<AutoCompleteTextView>(R.id.search_src_text)?.apply{
        threshold = 0
    }

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
                vm.onQuerySubmit(trimQuery(query))
                return true
            }
        })
        searchViewEditText?.setOnFocusChangeListener { v, hasFocus ->
            DBG("searchViewEditText focus = $hasFocus")
            if(hasFocus) {
                imm?.showSoftInput(v, 0)
                vm.tryReset()
            }
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
            is ViewStateTranslate.Error -> viewState.query
            else -> toolbar.resources.getString(R.string.appName)
        }
        toolbar.subtitle = when(viewState){
            is ViewStateTranslate.Data -> viewState.translation.transcription?.let{"[$it]"}
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

}

/*
    val clipboard = getClipboardText(rootView.context)
    DBG("Clipboard: $clipboard")
    searchView.suggestionsAdapter = clipboard?.let{
        SimpleCursorAdapter(searchView.context,
            android.R.layout.simple_list_item_1,
            MatrixCursor(arrayOf(BaseColumns._ID, "suggestion")).apply {
                addRow(arrayOf(0, clipboard))
                addRow(arrayOf(1, "hello"))
            },
            arrayOf("suggestion"),
            intArrayOf(android.R.id.text1),
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)
    }
 */
