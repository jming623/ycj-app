package compose.navigation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import compose.ui.EditMediaView

class EditMediaComponent(
    private val rootComponent: RootComponent,
    componentContext: ComponentContext,
) : ComponentContext by componentContext{


    @Composable
    fun showView() {
        EditMediaView(
            editMediaComponent = this,
        )
    }
}