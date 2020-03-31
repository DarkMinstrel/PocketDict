package com.darkminstrel.pocketdict.ui.frg_list

import android.util.LruCache
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.darkminstrel.pocketdict.Config
import com.darkminstrel.pocketdict.R
import com.darkminstrel.pocketdict.colorize
import com.darkminstrel.pocketdict.data.ParsedTranslation
import com.darkminstrel.pocketdict.ui.views.ViewHolderText
import com.darkminstrel.pocketdict.ui.views.ViewHolderTextPair
import kotlinx.coroutines.*
import java.lang.RuntimeException
import java.util.*
import kotlin.collections.ArrayList

class AdapterFavorites(private val vm: FrgListViewModel, private val onClickListener: (String)->Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var query = ""
    private val allKeys = ArrayList<String>()
    private val objects = ArrayList<Any>()
    private val jobsMap = IdentityHashMap<ViewHolderTextPair, Job>()
    private val cache = LruCache<String, ParsedTranslation>(Config.MEMORY_CACHE_SIZE)

    private val objectTitle = Object()

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
        val filteredKeys = ArrayList<String>()
        if(query.isEmpty()) filteredKeys.addAll(allKeys)
        else{
            filteredKeys.addAll(allKeys.filter{ it.startsWith(query) })
            filteredKeys.addAll(allKeys.filter{ it.contains(query) }.subtract(filteredKeys))
        }
        objects.clear()
        if(filteredKeys.isEmpty()){
            //TODO empty view
        }else{
            objects.add(objectTitle)
            objects.addAll(filteredKeys)
        }
        notifyDataSetChanged() //TODO optimize
    }

    override fun getItemCount(): Int = objects.size

    override fun getItemViewType(position: Int): Int {
        return when(objects[position]){
            objectTitle -> R.layout.listitem_title
            else -> R.layout.listitem_word
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when(viewType){
            R.layout.listitem_title -> ViewHolderText(view)
            R.layout.listitem_word -> ViewHolderTextPair(view)
            else -> throw RuntimeException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return when(val o = objects[position]){
            objectTitle -> (holder as ViewHolderText).setText(R.string.favoriteTranslations)
            else -> bindTextPair(holder as ViewHolderTextPair, o as String)
        }
    }

    private fun bindTextPair(holder: ViewHolderTextPair, key: String){
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

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if(holder is ViewHolderTextPair){
            jobsMap[holder]?.cancel()
            jobsMap.remove(holder)
        }
        super.onViewRecycled(holder)
    }
}