package compose.di

import compose.data.repos.MenuReposImpl
import compose.data.repos.MenuRepos
import compose.data.use_case.MenuUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

fun CommonModule() = networkModule() + module {
    // CommonModule은 networkModule함수가 반환하는 Koin Module과 아래 정의된 모듈을 결합하여 반환함.

    Napier.d("CommonModule 생성 시작")
    single<CoroutineDispatcher> { Dispatchers.IO }

    single<MenuRepos> { MenuReposImpl(get()) }

    single { MenuReposImpl(get()) }

    single { MenuUseCase(get()) }
}