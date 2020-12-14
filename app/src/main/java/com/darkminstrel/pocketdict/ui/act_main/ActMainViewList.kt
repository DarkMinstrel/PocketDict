package com.darkminstrel.pocketdict.ui.act_main

import android.graphics.drawable.RippleDrawable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.darkminstrel.pocketdict.R
import com.darkminstrel.pocketdict.data.ParsedTranslation
import com.darkminstrel.pocketdict.databinding.ActMainBinding
import kotlinx.coroutines.CoroutineScope

class ActMainViewList(scope: CoroutineScope, private val binding:ActMainBinding, private val vm: ActMainVM, private val clearFocus:()->Unit) {
    private val adapterFavorites = AdapterFavorites(scope, vm, ::onItemClicked)
    private val linearLayoutManager = LinearLayoutManager(binding.recyclerView.context)

    init {
        binding.recyclerView.apply {
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
    }

    private var pendingViewToHighlight:Int? = null

    private fun onItemClicked(position:Int, translation: ParsedTranslation){
        val offset = binding.recyclerView.resources.getDimensionPixelOffset(R.dimen.scroll_top_offset)
        linearLayoutManager.scrollToPositionWithOffset(position,offset)
        pendingViewToHighlight = position
        clearFocus.invoke()
        vm.onOpenDetails(translation)
    }

    fun setVisible(visible:Boolean){
        binding.recyclerView.visibility = if(visible) View.VISIBLE else View.GONE

        //show ripple effect
        if(visible){
            pendingViewToHighlight?.let{ position ->
                linearLayoutManager.findViewByPosition(position)?.let{ view ->
                    (view.background as? RippleDrawable)?.apply{
                        setHotspot((view.width/2).toFloat(), (view.height/2).toFloat())
                        state = intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled)
                        view.post { state = intArrayOf() }
                    }
                }
                pendingViewToHighlight = null
            }
        }
    }

    fun onQueryChanged(queryTrimmed:String){
        adapterFavorites.setQuery(queryTrimmed)
        binding.recyclerView.scrollTo(0, 0)
        linearLayoutManager.scrollToPositionWithOffset(0, 0)
    }

    fun setKeys(keys:List<String>) = adapterFavorites.setKeys(keys)
}