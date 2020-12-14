package com.darkminstrel.pocketdict.ui

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.darkminstrel.pocketdict.Config
import com.darkminstrel.pocketdict.R
import com.darkminstrel.pocketdict.databinding.ActMainBinding
import com.darkminstrel.pocketdict.ui.act_main.ActMainVM
import com.darkminstrel.pocketdict.ui.act_main.ActMainView
import org.koin.android.viewmodel.ext.android.viewModel

class ActMain : AppCompatActivity(R.layout.act_main) {

    private val vm: ActMainVM by viewModel()
    private lateinit var view: ActMainView

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme) //removing the splash
        if(Config.ENABLE_IME_INSETS && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
            window.setDecorFitsSystemWindows(false)
        }
        super.onCreate(savedInstanceState)
        val binding = ActMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        setSupportActionBar(binding.toolbar)

        view = ActMainView(lifecycleScope, binding, vm)
        vm.liveDataFavoriteKeys.observe(this, { keys -> view.setKeys(keys) })
        vm.liveDataViewState.observe(this, { viewState -> view.setViewState(viewState) })
        vm.ttsManager.liveDataUttering.observe(this, { view.setSpeechState(it) })
    }

    override fun onResume() {
        super.onResume()
        view.requestFocus(true)
    }

    override fun onBackPressed() {
        if(vm.tryReset()) {
            view.tryClearInput()
            return
        }
        super.onBackPressed()
    }

}




