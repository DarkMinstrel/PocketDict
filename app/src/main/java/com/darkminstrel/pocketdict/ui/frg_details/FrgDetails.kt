package com.darkminstrel.pocketdict.ui.frg_details

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.darkminstrel.pocketdict.R
import com.darkminstrel.pocketdict.ui.FrgBase
import org.koin.android.viewmodel.ext.android.viewModel

private const val KEY_QUERY = "query"

class FrgDetails: FrgBase(R.layout.frg_details) {

    companion object {
        fun create(query:String):FrgDetails{
            return FrgDetails().apply {
                arguments = Bundle().apply { putString(KEY_QUERY, query) }
            }
        }
    }

    private val vm:FrgDetailsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState==null){
            arguments?.getString(KEY_QUERY)?.let{ vm.onQuerySubmit(it) }
        }
    }

    private var view: FrgDetailsView? = null

    override fun onViewCreated(rootView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)
        view = FrgDetailsView(rootView, vm)
        vm.getLiveDataViewState().observe(viewLifecycleOwner, Observer { viewState -> view?.setViewState(viewState) })
        vm.getLiveDataCacheKeys().observe(viewLifecycleOwner, Observer { keys -> view?.setKeys(keys) })
        vm.ttsManager.getLiveDataUttering().observe(viewLifecycleOwner, Observer { view?.setSpeechState(it) })
    }

    override fun onDestroyView() {
        view = null
        super.onDestroyView()
    }
}

