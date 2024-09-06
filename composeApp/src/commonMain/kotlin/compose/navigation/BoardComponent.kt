package compose.navigation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import compose.ui.BoardView
import compose.ui.GalleryView

class BoardComponent(
    private val rootComponent: RootComponent,
    componentContext: ComponentContext
): ComponentContext by componentContext {

    private fun onBackButtonClick() {
        rootComponent.pop()
    }

    @Composable
    fun showView() {
        BoardView(
            onBackButtonClick = { onBackButtonClick() },
        )
    }
}