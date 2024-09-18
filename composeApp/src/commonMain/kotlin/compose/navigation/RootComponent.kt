package compose.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import compose.data.repos.GalleryRepository
import compose.data.use_case.MenuUseCase
import compose.permissions.PermissionsController
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RootComponent(
    componentContext: ComponentContext,
    private val permissionsController: PermissionsController
): ComponentContext by componentContext, KoinComponent  { //여기에 KoinComponent 집어넣으니까 commonMain에서 inject함수가 먹음

    // Create Component
    private val navigation = StackNavigation<Configuration>()
    private val componentFactory = ComponentFactory(this, componentContext)
    private val menuComponent = componentFactory.createMenuComponent()
    private val settingsComponent = componentFactory.createSettingsComponent()
    private val galleryComponent = componentFactory.createGalleryComponent(permissionsController)
    private val boardComponent = componentFactory.createBoardComponent()
    private val editMediaComponent = componentFactory.createEditMediaComponent()

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
                menuComponent,
                onAddButtonClicked = { navigate(Configuration.BoardView) }
            )
            is Configuration.SettingsView -> Child.SettingsView(settingsComponent)
            is Configuration.GalleryView -> Child.GalleryView(galleryComponent)
            is Configuration.BoardView -> Child.BoardView(boardComponent)
            is Configuration.EditMediaView -> Child.EditMediaView(editMediaComponent)
        }

    }

    fun navigate(configuration: Configuration) {
        navigation.push(configuration)
    }

    fun pop() {
        navigation.pop()
    }

    sealed class Child {
        data class HomeView(val component: MenuComponent, val onAddButtonClicked: () -> Unit): Child()
        data class SettingsView(val component: SettingsComponent): Child()
        data class GalleryView(val component: GalleryComponent): Child()
        data class BoardView(val component: BoardComponent): Child()
        data class EditMediaView(val component: EditMediaComponent): Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data object HomeView : Configuration()
        @Serializable
        data object SettingsView : Configuration()
        @Serializable
        data object GalleryView: Configuration()
        @Serializable
        data object BoardView: Configuration()
        @Serializable
        data object EditMediaView: Configuration()
    }
}