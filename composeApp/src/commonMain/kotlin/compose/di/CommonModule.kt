package compose.di

import compose.data.repos.MenuReposImpl
import compose.data.repos.UserReposImpl
import compose.domain.repos.MenuRepos
import compose.domain.repos.UserRepos
import compose.domain.use_case.MenuUseCase
import compose.domain.use_case.UserUseCase
import compose.ui.view.user.UserVM
import compose.ui.view.user.UserView
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
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


//    single<UserRepos> {
//        UserReposImpl(get()) //HttpClient 객체를 파라미터로 받고 UserRepos를 반환
//    }
//    factory {
//        UserUseCase(get()) // 위에서 반환된 UserRepos를 매게변수로 받고 UserUseCase 클래스가 초기화 됨.
//    }
//
//    single { Dispatchers.Default }
//
//    single {
//        UserVM(get(), get())
//    }
}