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
    }

    override fun onResume() {
        super.onResume()
        vh.onResume()
    }
}



