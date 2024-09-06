package compose.navigation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import compose.ui.BoardView
import compose.ui.GalleryView

class BoardComponent(
    componentContext: ComponentContext,
): ComponentContext by componentContext {

    @Composable
    fun showView() {
        BoardView()
    }
}