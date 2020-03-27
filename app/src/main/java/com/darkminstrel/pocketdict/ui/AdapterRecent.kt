package com.darkminstrel.pocketdict.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.darkminstrel.pocketdict.R
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class AdapterRecent(private val vm:ActMainViewModel, private val onClickListener: (String)->Unit): RecyclerView.Adapter<ViewHolderTextPair>() {

    private val keys = ArrayList<String>()

    fun setCacheKeys(keys:List<String>){
        this.keys.clear()
        this.keys.addAll(keys)
        notifyDataSetChanged() //TODO optimize
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolderTextPair(LayoutInflater.from(parent.context).inflate(R.layout.listitem_word, parent, false))

    override fun getItemCount(): Int = keys.size

    private val jobsMap = IdentityHashMap<ViewHolderTextPair, Job>()

    override fun onBindViewHolder(holder: ViewHolderTextPair, position: Int) {
        val key = keys[position]
        holder.setTexts(key, "")
        holder.itemView.setOnClickListener { onClickListener.invoke(key) }

        (holder.itemView.tag as? Job)?.cancel()
        jobsMap[holder]?.cancel()
        jobsMap[holder] = CoroutineScope(Dispatchers.IO).launch {
            val description = vm.getCachedTranslation(key)?.getDescription()
            withContext(Dispatchers.Main){
                if(isActive) {
                    holder.setTexts(key, description?:"")
                    holder.animateAlpha()
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