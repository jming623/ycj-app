package compose.navigation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import compose.permissions.PermissionsController
import io.github.aakira.napier.Napier
import compose.ui.GalleryView

class GalleryComponent(
    private val rootComponent: RootComponent,
    componentContext: ComponentContext,
    private val permissionsController: PermissionsController
) {

    init {
        Napier.d("여기 실행됐을거고?")
        permissionsController.checkAndRequestPermissions{
            Napier.d("권한 요청 후 실행되는 작업")
        }
    }

    private fun onBackButtonClick() {
        rootComponent.pop()
    }

    @Composable
    fun showView() {
        GalleryView(
            galleryComponent = this,
            onBackButtonClick = { onBackButtonClick() },
        )
    }
}