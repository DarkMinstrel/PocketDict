package com.darkminstrel.pocketdict

import android.app.Application
import com.darkminstrel.pocketdict.api.ApiImpl
import com.darkminstrel.pocketdict.database.DatabaseRoom
import com.darkminstrel.pocketdict.database.Databaseable
import com.darkminstrel.pocketdict.ui.frg_list.FrgListViewModel
import com.darkminstrel.pocketdict.ui.frg_details.FrgDetailsViewModel
import com.darkminstrel.pocketdict.usecases.UsecaseTranslate
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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
            single{ Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }
            single{ ApiImpl().makeRetrofitService(get()) }
            single{ DatabaseRoom(get(), get()) as Databaseable }
        }
        val usecaseModule = module {
            factory{ UsecaseTranslate(get(), get()) }
        }
        val vmModule = module {
            viewModel{ FrgListViewModel(get()) }
            viewModel{ FrgDetailsViewModel(get()) }
        }

        return startKoin {
            if(BuildConfig.DEBUG_FEATURES) androidLogger()
            androidContext(this@App)
            modules(listOf(appModule, usecaseModule, vmModule))
        }
    }

}