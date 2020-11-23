package com.darkminstrel.pocketdict.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.darkminstrel.pocketdict.R
import com.darkminstrel.pocketdict.ui.act_main.ActMainVM
import com.darkminstrel.pocketdict.ui.act_main.ActMainView
import com.darkminstrel.pocketdict.utils.DBG
import org.koin.android.viewmodel.ext.android.viewModel

class ActMain : AppCompatActivity(R.layout.act_main) {

    private val vm: ActMainVM by viewModel()
    private var view: ActMainView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme) //removing the splash
        super.onCreate(savedInstanceState)
        val rootView = findViewById<View>(android.R.id.content)
        setSupportActionBar(findViewById(R.id.toolbar))
        //iterate(rootView) //TODO kill
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

    private fun iterate(view:View){
        view.setOnFocusChangeListener{v, isFocused ->
            if(isFocused) DBG("Focused: ${v.javaClass.simpleName}")
        }
        if(view is ViewGroup){
            for(v in view.children) iterate(v)
        }
    }
}



/*
init {
    if(Config.ENABLE_IME_INSETS) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        //setupInsets()
    }else{
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }
}

@RequiresApi(30)
fun setupInsets(){
    window.setDecorFitsSystemWindows(false)
    rootView.doOnLayout {
        rootView.rootWindowInsets?.let { insets -> applyInsets(insets) }
        rootView.setWindowInsetsAnimationCallback(object : WindowInsetsAnimation.Callback(DISPATCH_MODE_STOP) {
            override fun onProgress(insets: WindowInsets, animations: MutableList<WindowInsetsAnimation>): WindowInsets {
                applyInsets(insets)
                return insets
            }
        })
        //TODO floating keyboard
    }
}

@RequiresApi(30)
fun applyInsets(insets: WindowInsets){
    val top = insets.getInsets(WindowInsets.Type.systemBars()).top
    val bottom = max(insets.getInsets(WindowInsets.Type.ime()).bottom, insets.getInsets(WindowInsets.Type.navigationBars()).bottom)
    containerOutput.updateLayoutParams<ViewGroup.MarginLayoutParams> {
        updateMargins(top = top)
    }
    containerInput.updateLayoutParams<ViewGroup.MarginLayoutParams> {
        updateMargins(bottom = bottom)
    }
}
 */

