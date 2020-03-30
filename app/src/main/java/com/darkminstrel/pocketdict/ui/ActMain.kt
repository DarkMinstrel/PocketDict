package com.darkminstrel.pocketdict.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.darkminstrel.pocketdict.R
import com.darkminstrel.pocketdict.ui.frg_details.FrgDetails
import com.darkminstrel.pocketdict.ui.frg_list.FrgList

class ActMain : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)

        if(savedInstanceState==null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.containerFragment, FrgList())
                .commit()
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

