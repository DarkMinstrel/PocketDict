package com.darkminstrel.pocketdict.ui

import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AutoCompleteTextView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.darkminstrel.pocketdict.Config
import com.darkminstrel.pocketdict.DBG
import com.darkminstrel.pocketdict.R
import com.darkminstrel.pocketdict.trimQuery
import com.darkminstrel.pocketdict.usecases.ViewStateTranslate


class ActMainView(private val rootView: View, window: Window, private val vm: ActMainViewModel){
    private val scrollView = rootView.findViewById<ScrollView>(R.id.scrollView)
    private val containerOutput = rootView.findViewById<View>(R.id.containerOutput)
    private val containerInput = rootView.findViewById<View>(R.id.containerInput)
    private val progressBar = rootView.findViewById<View>(R.id.progressBar)
    private val tvError = rootView.findViewById<TextView>(R.id.tvError)
    private val vhData = ViewHolderData(rootView.findViewById(R.id.containerData), vm)
    private val adapterRecent = AdapterRecent(vm) {query -> searchView.setQuery(query, true)}
    private val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView).apply {
        layoutManager = LinearLayoutManager(context)
        adapter = adapterRecent
    }

    private val searchView = rootView.findViewById<SearchView>(R.id.searchView)
    private val searchViewEditText:AutoCompleteTextView? = searchView.findViewById<AutoCompleteTextView>(R.id.search_src_text)?.apply{
        threshold = 0
    }

    init {
        if(Config.ENABLE_IME_INSETS) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
            //setupInsets()
        }else{
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }

    private val queryTextListener = object: SearchView.OnQueryTextListener {
        override fun onQueryTextChange(newText: String):Boolean {
            vm.clearSearch()
            adapterRecent.setQuery(trimQuery(newText))
            return false
        }
        override fun onQueryTextSubmit(query: String): Boolean {
            vm.onQuerySubmit(trimQuery(query))
            return true
        }
    }

    /*
    @RequiresApi(30)
    fun setupInsets(){
        window.setDecorFitsSystemWindows(false)
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
    fun applyInsets(insets: WindowInsets){
        val top = insets.getInsets(WindowInsets.Type.systemBars()).top
        val bottom = max(insets.getInsets(WindowInsets.Type.ime()).bottom, insets.getInsets(WindowInsets.Type.navigationBars()).bottom)
        containerOutput.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMargins(top = top)
        }
        containerInput.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMargins(bottom = bottom)
        }
    }
     */

    fun tryClear():Boolean{
        return if(searchView.query.isNotEmpty()) {
            vm.clearSearch()
            searchView.setQuery("", false)
            searchView.requestFocus()
            //hack to force show keyboard
            searchView.isIconified = true
            searchView.isIconified = false
            true
        }else false
    }


    fun onResume() {
        searchViewEditText?.selectAll()
        searchView.requestFocus()

        //following lines must be called after restoring instance state
        searchView.setOnQueryTextListener(this.queryTextListener)
        adapterRecent.setQuery(trimQuery(searchView.query.toString()))
    }

    fun onPause(){
        searchView.setOnQueryTextListener(null)
    }

    fun setViewState(viewState: ViewStateTranslate) {
        DBG("View state: ${viewState.javaClass.simpleName}")
        progressBar.visibility = if(viewState is ViewStateTranslate.Progress) View.VISIBLE else View.INVISIBLE
        tvError.visibility = if(viewState is ViewStateTranslate.Error) View.VISIBLE else View.INVISIBLE
        scrollView.visibility = if(viewState is ViewStateTranslate.Data) View.VISIBLE else View.INVISIBLE
        recyclerView.visibility = if(viewState is ViewStateTranslate.Empty) View.VISIBLE else View.INVISIBLE

        if(viewState is ViewStateTranslate.Error){
            tvError.text = viewState.error.getMessage(tvError.context)
        }else if(viewState is ViewStateTranslate.Data){
            searchView.clearFocus()
            scrollView.scrollTo(0,0)
            vhData.setData(viewState.translation)
        }
    }

    fun setCacheKeys(keys:List<String>){
        DBG("Keys: $keys")
        adapterRecent.setCacheKeys(keys)
        vhData.setCacheKeys(keys)
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

}