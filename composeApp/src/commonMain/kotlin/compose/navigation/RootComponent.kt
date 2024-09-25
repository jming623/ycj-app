package compose.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.active
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import compose.data.repos.GalleryRepository
import compose.data.repos.MediaFile
import compose.data.use_case.MenuUseCase
import compose.permissions.PermissionsController
import compose.util.ImageManager
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
    
    // 게시글 등록 시, 이미지 처리를 관리할 클래스
    val imageManager = ImageManager()

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
        // 스택을 조작하는 navigate 함수 사용
        navigation.navigate(
            transformer = { stack ->
                // 현재 스택에서 중복된 페이지를 제외하고, 중복된 페이지를 스택 끝에 추가
                val filteredStack = stack.filter { it != configuration } // 중복된 페이지를 제거
                filteredStack + configuration // 제거된 페이지를 다시 스택 끝에 추가
            },
            onComplete = { newStack, oldStack ->
                // 여기에서 필요한 경우 추가 작업 수행
                Napier.d("Navigation updated: $newStack")
            }
        )
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