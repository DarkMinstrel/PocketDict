package com.darkminstrel.pocketdict

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.darkminstrel.pocketdict.ui.main.ScreenMain
import com.darkminstrel.pocketdict.ui.main.ViewModelMain
import com.darkminstrel.pocketdict.ui.theme.PocketTheme
import org.koin.androidx.compose.getViewModel

class ActMain : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)
        setContent {
            PocketTheme {
                val viewModel: ViewModelMain = getViewModel()
                ScreenMain(props = viewModel.props.value)
            }
        }
    }
}
