package compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import compose.data.repos.MediaFile
import compose.ui.EditMediaView
import io.github.aakira.napier.Napier

class EditMediaComponent(
    private val rootComponent: RootComponent,
    componentContext: ComponentContext,
) : ComponentContext by componentContext{

    fun moveToGalleryView() {
        rootComponent.navigate(RootComponent.Configuration.GalleryView)
    }

    fun moveToBoardView() {
        rootComponent.navigate(RootComponent.Configuration.BoardView)
    }

    @Composable
    fun showView() {
        EditMediaView(
            rootComponent = rootComponent,
            editMediaComponent = this,
        )
    }
}