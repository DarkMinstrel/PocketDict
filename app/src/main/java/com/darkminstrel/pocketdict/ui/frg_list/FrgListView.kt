package com.darkminstrel.pocketdict.ui.frg_list

import android.view.View
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.darkminstrel.pocketdict.R
import com.darkminstrel.pocketdict.trimQuery

class FrgListView(rootView: View, vm: FrgListViewModel, onSubmit:(String)->Unit) {

    private val searchView = rootView.findViewById<SearchView>(R.id.searchView)
    private val searchViewEditText: AutoCompleteTextView? = searchView.findViewById<AutoCompleteTextView>(R.id.search_src_text)?.apply{
        threshold = 0
    }

    private val adapterFavorites = AdapterFavorites(vm, onSubmit)
    private val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView).apply {
        layoutManager = LinearLayoutManager(context)
        adapter = adapterFavorites
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int){
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    searchView.clearFocus()
                }
            }
        })
    }

    private val queryTextListener = object: SearchView.OnQueryTextListener {
        override fun onQueryTextChange(newText: String):Boolean {
            adapterFavorites.setQuery(trimQuery(newText))
            return false
        }
        override fun onQueryTextSubmit(query: String): Boolean {
            onSubmit(trimQuery(query))
            return true
        }
    }

    //TODO kill?
//    fun tryClear():Boolean{
//        return if(searchView.query.isNotEmpty()) {
//            vm.clearSearch()
//            searchView.setQuery("", false)
//            searchView.requestFocus()
//            //hack to force show keyboard
//            searchView.isIconified = true
//            searchView.isIconified = false
//            true
//        }else false
//    }

    fun onResume() {
        searchViewEditText?.selectAll()
        searchView.requestFocus()

        //following lines must be called after restoring instance state
        searchView.setOnQueryTextListener(this.queryTextListener)
        adapterFavorites.setQuery(trimQuery(searchView.query.toString()))
    }

    fun onPause(){
        searchView.setOnQueryTextListener(null)
    }

    fun setKeys(keys:List<String>) = adapterFavorites.setKeys(keys)

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