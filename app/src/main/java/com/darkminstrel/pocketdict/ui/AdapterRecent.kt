package com.darkminstrel.pocketdict.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.darkminstrel.pocketdict.R
import com.darkminstrel.pocketdict.colorize
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class AdapterRecent(private val vm:ActMainViewModel, private val onClickListener: (String)->Unit): RecyclerView.Adapter<ViewHolderTextPair>() {
    private var query = ""
    private val allKeys = ArrayList<String>()
    private val filteredKeys = ArrayList<String>()

    fun setCacheKeys(keys:List<String>){
        this.allKeys.clear()
        this.allKeys.addAll(keys)
        refresh()
    }

    fun setQuery(query:String){
        this.query = query
        refresh()
    }

    private fun refresh(){
        filteredKeys.clear()
        if(query.isEmpty()) filteredKeys.addAll(allKeys)
        else{
            filteredKeys.addAll(allKeys.filter{ it.startsWith(query) })
            filteredKeys.addAll(allKeys.filter{ it.contains(query) }.subtract(filteredKeys))
        }
        notifyDataSetChanged() //TODO optimize
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolderTextPair(LayoutInflater.from(parent.context).inflate(R.layout.listitem_word, parent, false))

    override fun getItemCount(): Int = filteredKeys.size

    private val jobsMap = IdentityHashMap<ViewHolderTextPair, Job>()

    override fun onBindViewHolder(holder: ViewHolderTextPair, position: Int) {
        val key = filteredKeys[position]
        val keyColorized = if(query.isEmpty()) key else colorize(holder.itemView.context, key, query)
        holder.setTexts(keyColorized, "")
        holder.itemView.setOnClickListener { onClickListener.invoke(key) }

        (holder.itemView.tag as? Job)?.cancel()
        jobsMap[holder]?.cancel()
        jobsMap[holder] = CoroutineScope(Dispatchers.IO).launch {
            val description = vm.getFavorite(key)?.getDescription()
            withContext(Dispatchers.Main){
                if(isActive) {
                    holder.setTexts(keyColorized, description?:"")
                }
            }
        }
    }

    override fun onViewRecycled(holder: ViewHolderTextPair) {
        jobsMap[holder]?.cancel()
        jobsMap.remove(holder)
        super.onViewRecycled(holder)
    }
}