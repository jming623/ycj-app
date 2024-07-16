package compose.di

import compose.data.repos.UserReposImpl
import compose.domain.repos.UserRepos
import compose.domain.use_case.UserUseCase
import compose.ui.view.user.UserVM
import compose.ui.view.user.UserView
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

fun CommonModule() = networkModule() + module {
    // CommonModule은 networkModule함수가 반환하는 Koin Module과 아래 정의된 모듈을 결합하여 반환함.

    single<UserRepos> {
        UserReposImpl(get()) //HttpClient 객체를 파라미터로 받고 UserRepos를 반환
    }
    factory {
        UserUseCase(get()) // 위에서 반환된 UserRepos를 매게변수로 받고 UserUseCase 클래스가 초기화 됨.
    }

//    single {
//        UserUseCase(get())
//    }

    single { Dispatchers.Default }

    single {
        UserVM(get(), get())
    }

//    viewModel {
//        UserVM(get<UserUseCase>(), Dispatchers.Default)
//    }

//    single {
//        ProductsRemoteDataSource(get())
//    }

//    single {
//        /*
//        * Single 블록은 객체를 Koin 컨테이너에 등록하여 하나의 인스턴스를 보장함.(싱글톤)
//        * get() 알맞은 의존성을 주입해줌.
//        */
//        // Added Dependency Injection With Koin에서 추가됨:
//        // HomeRepository는 매개변수로 httpClient를 전달받아야하는데, Koin이 자동으로 networkModule내부에 정의된 httpClient를 주입 해주게 됨.
//        // Added Cache Database With SqlDelight에서 추가됨:
//        // HomeRepository는 매개변수로 productsLocalDataSource과 productsRemoteDataSource를 전달받음. 이 두 값은 위에 선언 되어있음
//        HomeRepository(get(), get())
//    }
//
//    single {
//        HomeViewModel(get()) // HomeViewModel은 매개변수로 HomeRepository를 전달받아야하는데, Koin이 자동으로 위에 모듈 정의부에 정의된 HomeRepository를 주입 해주게 됨.
//    }
//
//    single<RootComponent> {
//        DefaultRootComponent(
//            componentContext = get(),
//            homeViewModel = get()
//        )
//    }

    // 위에서 사용된 single키워드 외에도 요청할 때마다 매번 새로운 객체를 생성하는 factory키워드 / viewModel을 대상으로 제공되는 viewModel키워드가 존재한다.
}