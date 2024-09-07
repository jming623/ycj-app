package compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import compose.ui.BoardView
import compose.ui.GalleryView

class BoardComponent(
    private val rootComponent: RootComponent,
    componentContext: ComponentContext,
    private val moveToGallery: () -> Unit
): ComponentContext by componentContext {

    private fun onBackButtonClick() {
        rootComponent.pop()
    }
    fun onGalleryButtonClick() {
        moveToGallery()
    }

    @Composable
    fun showView() {
        BoardView(
            onBackButtonClick = { onBackButtonClick() },
            onGalleryButtonClick = {onGalleryButtonClick() },
        )
    }
}