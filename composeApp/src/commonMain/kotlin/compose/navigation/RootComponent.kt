package compose.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import compose.data.use_case.MenuUseCase
import kotlinx.coroutines.CoroutineDispatcher
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
                    rootComponent = this,
                    componentContext = context,
                    defaultDispatcher = defaultDispatcher,
                    menuUseCase = menuUseCase
                )
            )
        is Configuration.SettingsView -> Child.SettingsView(
                SettingsComponent(
                    rootComponent = this,
                    componentContext = context,
                    defaultDispatcher = defaultDispatcher
                )
            )
        }
    }

    fun navigate(configuration: Configuration) {
        navigation.push(configuration)
    }

    fun pop() {
        navigation.pop()
    }

    sealed class Child {
        data class HomeView(val component: MenuComponent): Child()
        data class SettingsView(val component: SettingsComponent): Child()
    }

    @Serializable
    sealed class Configuration {

        @Serializable
        data object HomeView : Configuration()

        @Serializable
        data object SettingsView : Configuration()
    }
}