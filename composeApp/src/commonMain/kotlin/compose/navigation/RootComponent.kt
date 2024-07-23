package compose.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import compose.domain.repos.MenuRepos
import compose.domain.use_case.MenuUseCase
import data.Menu
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RootComponent(
    componentContext: ComponentContext
): ComponentContext by componentContext, KoinComponent  { //여기에 KoinComponent 집어넣으니까 commonMain에서 inject함수가 먹음

    private val defaultDispatcher: CoroutineDispatcher by inject()
    private val menuUseCase: MenuUseCase by inject()

    private val navigation = StackNavigation<Configuration>()
    val childStack = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        initialConfiguration = Configuration.HomeView,
        handleBackButton = true,
        childFactory = ::createChild
    )

    @OptIn(ExperimentalDecomposeApi::class)
    private fun createChild(
        config: Configuration,
        context: ComponentContext
    ): Child {
        return when(config) {
            Configuration.HomeView -> Child.HomeView(
                MenuComponent(
                    componentContext = context,
                    defaultDispatcher = defaultDispatcher,
                    menuUseCase = menuUseCase
                )
            )
//            Configuration.ScreenA -> Child.ScreenA(
//                ScreenAComponent(
//                    componentContext = context,
//                    onNavigateToScreenB = { text ->
//                        navigation.pushNew(Configuration.ScreenB(text))
//                    }
//                )
//            )
//            is Configuration.ScreenB -> Child.ScreenB(
//                ScreenBComponent(
//                    text = config.text,
//                    componentContext = context,
//                    onGoBack = {
//                        navigation.pop()
//                    }
//                )
//            )
        }
    }

    sealed class Child {
        data class HomeView(val component: MenuComponent): Child()
//        data class ScreenA(val component: ScreenAComponent): Child()
//        data class ScreenB(val component: ScreenBComponent): Child()
    }

    @Serializable
    sealed class Configuration {

        @Serializable
        data object HomeView : Configuration()

//        @Serializable
//        data object ScreenA: Configuration()
//
//        @Serializable
//        data class ScreenB(val text: String): Configuration()
    }
}