package compose.navigation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import compose.ui.HomeView

class HomeComponent(
    componentContext: ComponentContext
): ComponentContext by componentContext {

    @Composable
    fun showView() {
        HomeView(
        )
    }

}