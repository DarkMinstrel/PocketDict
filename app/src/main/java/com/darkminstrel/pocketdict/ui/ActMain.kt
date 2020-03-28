package com.darkminstrel.pocketdict.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.darkminstrel.pocketdict.R
import org.koin.android.viewmodel.ext.android.getViewModel

class ActMain : AppCompatActivity() {

    private lateinit var vh: ActMainView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)

        val vm = getViewModel<ActMainViewModel>()
        vh = ActMainView(findViewById(android.R.id.content), window, vm)
        vm.getLiveDataTranslate().observe(this, Observer { viewState -> vh.setViewState(viewState) })
        vm.getLiveDataCacheKeys().observe(this, Observer { keys -> vh.setCacheKeys(keys) })
    }

    override fun onPause() {
        super.onPause()
        vh.onPause()
    }

    override fun onResume() {
        super.onResume()
        vh.onResume()
    }

    override fun onBackPressed() {
        if(!vh.tryClear()) super.onBackPressed()
    }


}



