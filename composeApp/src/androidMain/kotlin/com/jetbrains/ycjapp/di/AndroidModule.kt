package com.jetbrains.ycjapp.di

import coil.ImageLoader
import coil.decode.SvgDecoder
import com.jetbrains.ycjapp.AndroidGalleryRepository
import compose.data.repos.GalleryRepository
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single<GalleryRepository> { AndroidGalleryRepository(get()) }
    single {
        ImageLoader.Builder(androidContext()) // Context를 주입받아 ImageLoader 생성
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
    }
}