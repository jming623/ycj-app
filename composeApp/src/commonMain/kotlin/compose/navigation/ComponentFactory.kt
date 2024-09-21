package compose.navigation

import com.arkivanov.decompose.ComponentContext
import compose.data.repos.GalleryRepository
import compose.data.use_case.MenuUseCase
import compose.permissions.PermissionsController
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

// RootComponent에서 사용될 Component들을 관리하는 클래스
class ComponentFactory (
    private val rootComponent: RootComponent,
    private val componentContext: ComponentContext,
): KoinComponent {

    private val defaultDispatcher: CoroutineDispatcher by inject()
    private val menuUseCase: MenuUseCase by inject()
    private val galleryRepository: GalleryRepository by inject()

    fun createMenuComponent(): MenuComponent {
        return MenuComponent(
            rootComponent = rootComponent,
            componentContext = componentContext,
            defaultDispatcher = defaultDispatcher,
            menuUseCase = menuUseCase
        )
    }
    fun createSettingsComponent(): SettingsComponent {
        return SettingsComponent(
            rootComponent = rootComponent,
            componentContext = componentContext,
            defaultDispatcher = defaultDispatcher
        )
    }
    fun createGalleryComponent(permissionsController: PermissionsController): GalleryComponent {
        return GalleryComponent(
            rootComponent = rootComponent,
            componentContext = componentContext,
            permissionsController = permissionsController,
            galleryRepository = galleryRepository
        )
    }
    fun createBoardComponent(): BoardComponent {
        return BoardComponent(
            rootComponent = rootComponent,
            componentContext = componentContext,
        )
    }
    fun createEditMediaComponent(): EditMediaComponent {
        return EditMediaComponent(
            rootComponent = rootComponent,
            componentContext = componentContext,
        )
    }
}