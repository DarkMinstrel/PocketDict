package com.darkminstrel.pocketdict.ui.frg_list

import android.util.LruCache
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.darkminstrel.pocketdict.Config
import com.darkminstrel.pocketdict.R
import com.darkminstrel.pocketdict.colorize
import com.darkminstrel.pocketdict.data.ParsedTranslation
import com.darkminstrel.pocketdict.ui.views.ViewHolderTextPair
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class AdapterFavorites(private val vm: FrgListViewModel, private val onClickListener: (String)->Unit): RecyclerView.Adapter<ViewHolderTextPair>() {
    private var query = ""
    private val allKeys = ArrayList<String>()
    private val filteredKeys = ArrayList<String>()
    private val jobsMap = IdentityHashMap<ViewHolderTextPair, Job>()
    private val cache = LruCache<String, ParsedTranslation>(Config.MEMORY_CACHE_SIZE)

    fun setKeys(keys:List<String>){
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

    override fun onBindViewHolder(holder: ViewHolderTextPair, position: Int) {
        val key = filteredKeys[position]
        val keyColorized = if(query.isEmpty()) key else colorize(holder.itemView.context, key, query)
        holder.itemView.setOnClickListener { onClickListener.invoke(key) }

        jobsMap[holder]?.cancel()
        cache.get(key)?.let{
            holder.setTexts(keyColorized, it.getDescription())
        } ?: run {
            holder.setTexts("", "")
            jobsMap[holder] = CoroutineScope(Dispatchers.IO).launch {
                val translation = vm.getFavorite(key)
                withContext(Dispatchers.Main){
                    translation?.let{
                        cache.put(key, translation)
                        if(isActive) holder.setTexts(keyColorized, it.getDescription())
                    }
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