package com.darkminstrel.pocketdict.ui.act_main

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.darkminstrel.pocketdict.R
import kotlinx.coroutines.CoroutineScope

class ActMainViewList(scope: CoroutineScope, rootView: View, vm: ActMainVM, clearFocus:()->Unit) {
    private val adapterFavorites = AdapterFavorites(scope, vm, clearFocus)
    private val linearLayoutManager = LinearLayoutManager(rootView.context)
    private val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView).apply {
        layoutManager = linearLayoutManager
        adapter = adapterFavorites
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int){
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    clearFocus.invoke()
                }
            }
        })
    }

    fun setVisible(visible:Boolean){
        recyclerView.visibility = if(visible) View.VISIBLE else View.GONE
    }


    fun onQueryChanged(queryTrimmed:String){
        adapterFavorites.setQuery(queryTrimmed)
        recyclerView.scrollTo(0, 0)
        linearLayoutManager.scrollToPositionWithOffset(0, 0)
    }

    fun setKeys(keys:List<String>) = adapterFavorites.setKeys(keys)
}