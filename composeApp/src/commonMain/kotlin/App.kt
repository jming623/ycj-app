import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import compose.navigation.RootComponent
import compose.ui.HomeView
import compose.ui.SettingsView
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App(root: RootComponent) {
    MaterialTheme {
        val childStack by root.childStack.subscribeAsState()
        Children (
            stack = childStack,
            animation = stackAnimation(slide())
        ) { child ->
            when(val instance = child.instance) {
                is RootComponent.Child.HomeView -> HomeView(
                    instance.component,
                    onNavigateToSettings = { root.navigate(RootComponent.Configuration.SettingsView) },
                    onAddButtonClicked = instance.onAddButtonClicked
                )
                is RootComponent.Child.SettingsView -> {
                    val settingsComponent = instance.component
                    settingsComponent.showView()
                }
                is RootComponent.Child.GalleryView -> {
                    val galleryComponent = instance.component
                    galleryComponent.showView()
                }
                is RootComponent.Child.BoardView -> {
                    val BoardComponent = instance.component
                    BoardComponent.showView()
                }
                is RootComponent.Child.EditMediaView -> {
                    val EditMediaComponent = instance.component
                    EditMediaComponent.showView()
                }
            }
        }
    }
}