package com.darkminstrel.pocketdict.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.darkminstrel.pocketdict.Config
import com.darkminstrel.pocketdict.R
import com.darkminstrel.pocketdict.ui.act_main.ActMainVM
import com.darkminstrel.pocketdict.ui.act_main.ActMainView
import org.koin.android.viewmodel.ext.android.viewModel

class ActMain : AppCompatActivity(R.layout.act_main) {

    private val vm: ActMainVM by viewModel()
    private var view: ActMainView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme) //removing the splash
        if(Config.ENABLE_IME_INSETS && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
            window.setDecorFitsSystemWindows(false)
        }
        super.onCreate(savedInstanceState)
        val rootView = findViewById<View>(android.R.id.content)
        setSupportActionBar(findViewById(R.id.toolbar))

        view = ActMainView(lifecycleScope, rootView, vm)
        vm.liveDataFavoriteKeys.observe(this, { keys -> view?.setKeys(keys) })
        vm.liveDataViewState.observe(this, { viewState -> view?.setViewState(viewState) })
        vm.ttsManager.liveDataUttering.observe(this, { view?.setSpeechState(it) })
    }

    override fun onResume() {
        super.onResume()
        view?.requestFocus()
    }

    override fun onBackPressed() {
        if(vm.tryReset()) return
        view?.let { if(it.tryClearInput()) return }
        super.onBackPressed()
    }

    override fun onDestroy() {
        view = null
        super.onDestroy()
    }

}




