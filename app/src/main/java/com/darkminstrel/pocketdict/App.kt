package com.darkminstrel.pocketdict

import android.app.Application
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.darkminstrel.pocketdict.api.leo.ApiLeo
import com.darkminstrel.pocketdict.api.reverso.ApiReverso
import com.darkminstrel.pocketdict.ui.main.ViewModelMain
import com.darkminstrel.pocketdict.usecases.UsecaseTranslate
import com.darkminstrel.pocketdict.usecases.UsecaseTranslateImpl
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }
    }
}

private val appModule = module {
    single { TextToSpeechManager(context = get()) }

    single<OkHttpClient> {
        OkHttpClient.Builder()
            .addInterceptor(ChuckerInterceptor.Builder(context = get()).build())
            .build()
    }
    single { Moshi.Builder().build() }
    single { ApiReverso.build(get(), get()) }
    single { ApiLeo.build(get(), get()) }
    single<HistoryRepository> {
        HistoryRepositoryImpl(datastore = DataStoreFactory.create(
            serializer = datastoreSerializer(moshi = get(), defaultValue = History()),
            produceFile = { androidApplication().dataStoreFile("translations") }
        ))
    }

    single<UsecaseTranslate> {
        UsecaseTranslateImpl(
            apiReverso = get(),
            apiLeo = get(),
            historyRepository = get(),
        )
    }

    viewModel {
        ViewModelMain(usecase = get(), tts = get())
    }

}