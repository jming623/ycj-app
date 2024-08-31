package com.jetbrains.ycjapp.di

import com.jetbrains.ycjapp.AndroidGalleryRepository
import compose.data.repos.GalleryRepository
import org.koin.dsl.module

val androidModule = module {
    single<GalleryRepository> { AndroidGalleryRepository(get()) }
}