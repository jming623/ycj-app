package com.jetbrains.ycjapp

import android.app.Application
import com.jetbrains.ycjapp.di.androidModule
import compose.di.initKoin
import initLogger
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.stopKoin

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        // Napier 초기화
        initLogger()

        Napier.d("Try Init Koin")
        initKoin (
            additionalModules = listOf(androidModule)
        ) {
            androidContext(this@MainApplication)
            androidLogger()
        }
    }
    override fun onTerminate() {
        super.onTerminate()
        stopKoin() // 애플리케이션 종료 시 Koin 정리
    }
}