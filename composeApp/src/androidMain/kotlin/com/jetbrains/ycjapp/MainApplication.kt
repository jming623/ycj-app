package com.jetbrains.ycjapp

import android.app.Application
import compose.di.CommonModule
import compose.di.initKoin
import initLogger
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import timber.log.Timber
import org.koin.dsl.module

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        // Napier 초기화
        initLogger()

        Napier.d("Try Init Koin")
        initKoin {
            androidContext(this@MainApplication)
            androidLogger()
        }
        Timber.plant(Timber.DebugTree())
    }
    override fun onTerminate() {
        super.onTerminate()
        stopKoin() // 애플리케이션 종료 시 Koin 정리
    }
}