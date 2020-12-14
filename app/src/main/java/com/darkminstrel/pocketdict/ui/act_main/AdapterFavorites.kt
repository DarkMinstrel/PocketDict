package com.darkminstrel.pocketdict.ui.act_main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.darkminstrel.pocketdict.*
import com.darkminstrel.pocketdict.data.ParsedTranslation
import com.darkminstrel.pocketdict.ui.views.ViewHolderSimple
import com.darkminstrel.pocketdict.ui.views.ViewHolderText
import com.darkminstrel.pocketdict.ui.views.ViewHolderTextPair
import com.darkminstrel.pocketdict.utils.assertUiThread
import com.darkminstrel.pocketdict.utils.colorize
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class AdapterFavorites(private val scopeView: CoroutineScope, private val vm: ActMainVM, private val onItemClicked: (position:Int, translation:ParsedTranslation) -> Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var query = ""
    private val allKeys = ArrayList<String>()
    private val objects = ArrayList<Any>()
    private val jobsMap = IdentityHashMap<ViewHolderTextPair, Job>()

    private val objectTitle = Object()
    private val objectNothingFound = Object()
    private val objectEmpty = Object()

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
        objects.clear()
        val filteredKeys = ArrayList<String>()
        if(query.isEmpty()) filteredKeys.addAll(allKeys)
        else{
            filteredKeys.addAll(allKeys.filter{ it.startsWith(query) })
            filteredKeys.addAll(allKeys.filter{ it.contains(query) }.subtract(filteredKeys))
        }
        if(filteredKeys.isNotEmpty()) {
            objects.add(objectTitle)
            objects.addAll(filteredKeys)
        }else if(filteredKeys.isEmpty() && allKeys.isNotEmpty()){
            objects.add(objectNothingFound)
        }else{
            objects.add(objectEmpty)
        }
        notifyDataSetChanged() //TODO optimize
    }

    override fun getItemCount(): Int = objects.size

    override fun getItemViewType(position: Int): Int {
        return when(objects[position]){
            objectTitle -> R.layout.listitem_title
            objectNothingFound -> R.layout.listitem_nothing_found
            objectEmpty -> R.layout.listitem_empty
            else -> R.layout.listitem_word
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when(viewType){
            R.layout.listitem_title -> ViewHolderText(view)
            R.layout.listitem_word -> ViewHolderTextPair(view)
            R.layout.listitem_nothing_found -> ViewHolderSimple(view)
            R.layout.listitem_empty -> ViewHolderSimple(view)
            else -> throw RuntimeException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val o = objects[position]
        when(holder){
            is ViewHolderTextPair -> bindTextPair(holder, o as String)
            is ViewHolderText -> {
                when(o){
                    objectTitle -> holder.setText(R.string.favoriteTranslations)
                }
            }
        }
    }

    private fun bindTextPair(holder: ViewHolderTextPair, key: String){
        holder.itemView.tag = null
        val keyColorized = if(query.isEmpty()) key else colorize(holder.itemView.context, key, query)
        holder.itemView.setOnClickListener { view->
            (view.tag as? ParsedTranslation)?.let{
                onItemClicked(holder.adapterPosition, it)
            }
        }

        jobsMap[holder]?.cancel()
        holder.setTexts("", "")
        jobsMap[holder] = scopeView.launch {
            vm.getFavorite(key)?.let { translation ->
                ensureActive()
                assertUiThread()
                holder.setTexts(keyColorized, translation.getDescription())
                holder.itemView.tag = translation
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