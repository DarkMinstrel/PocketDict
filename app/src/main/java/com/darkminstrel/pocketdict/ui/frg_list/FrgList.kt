package com.darkminstrel.pocketdict.ui.frg_list

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.darkminstrel.pocketdict.R
import com.darkminstrel.pocketdict.ui.FrgBase
import com.darkminstrel.pocketdict.ui.frg_details.FrgDetails
import org.koin.android.viewmodel.ext.android.viewModel

class FrgList: FrgBase(R.layout.frg_list) {

    private val vm:FrgListViewModel by viewModel()
    private var view:FrgListView? = null

    override fun onViewCreated(rootView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)
        view = FrgListView(lifecycleScope, rootView, vm, this::onSubmit)
        vm.getLiveDataCacheKeys().observe(viewLifecycleOwner, Observer { keys -> view?.setKeys(keys) })
    }

    private fun onSubmit(query:String){
        parentFragmentManager.beginTransaction()
            .replace(R.id.containerFragment, FrgDetails.create(query))
            .addToBackStack(null)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        view?.onResume()
    }

    override fun onDestroyView() {
        view = null
        super.onDestroyView()
    }
}