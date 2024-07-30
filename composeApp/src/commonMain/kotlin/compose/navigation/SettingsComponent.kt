package compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import compose.ui.SettingsView
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsComponent(
    private val rootComponent: RootComponent,
    componentContext: ComponentContext,
    private val defaultDispatcher: CoroutineDispatcher
): ComponentContext by componentContext  {

    private var _menuName = MutableStateFlow("")
    val menuName = _menuName.asStateFlow()

    private var _menuOrder = MutableStateFlow("")
    val menuOrder = _menuOrder.asStateFlow()

    private fun onBackButtonClick() {
        rootComponent.pop()
    }

    @Composable
    fun showView() {
        val menuName by menuName.collectAsState()
        val menuOrder by menuOrder.collectAsState()

        SettingsView(
            onBackButtonClick = { onBackButtonClick() },
            menuName = menuName,
            onMenuNameChange = { _menuName.value = it },
            menuOrder = menuOrder,
            onMenuOrderChange = { _menuOrder.value = it }
        )
    }
}