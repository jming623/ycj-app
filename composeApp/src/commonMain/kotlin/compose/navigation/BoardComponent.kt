package compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import compose.ui.BoardView
import compose.ui.GalleryView

class BoardComponent(
    private val rootComponent: RootComponent,
    componentContext: ComponentContext,
): ComponentContext by componentContext {

    fun onBackButtonClick() {
        rootComponent.navigate(RootComponent.Configuration.HomeView)
    }
    fun onGalleryButtonClick() {
        rootComponent.navigate(RootComponent.Configuration.GalleryView)
    }
    fun removeAllSelectedImages(){
        rootComponent.imageManager.removeAllSelectedImage()
    }

    @Composable
    fun showView() {
        BoardView(
            rootComponent = rootComponent,
            boardComponent = this
        )
    }
}