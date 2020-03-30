package com.darkminstrel.pocketdict

import android.app.Application
import com.darkminstrel.pocketdict.api.ApiImpl
import com.darkminstrel.pocketdict.database.DatabaseRoom
import com.darkminstrel.pocketdict.database.Databaseable
import com.darkminstrel.pocketdict.ui.ActMainViewModel
import com.darkminstrel.pocketdict.usecases.UsecaseTranslate
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        setupKoin()
    }

    private fun setupKoin(): KoinApplication {
        val appModule = module {
            single{ ApiImpl().makeRetrofitService() }
            single{ DatabaseRoom(get()) }
        }
        val usecaseModule = module {
            factory{ UsecaseTranslate(get(), get()) }
        }
        val vmModule = module {
            viewModel{ ActMainViewModel(get()) }
        }

        return startKoin {
            if(BuildConfig.DEBUG_FEATURES) androidLogger()
            androidContext(this@App)
            modules(listOf(appModule, usecaseModule, vmModule))
        }
    }

}