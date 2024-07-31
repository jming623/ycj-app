package compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import compose.data.repos.MenuReposImpl
import compose.domain.ApiResult
import compose.ui.SettingsView
import data.Menu
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsComponent(
    private val rootComponent: RootComponent,
    componentContext: ComponentContext,
    private val defaultDispatcher: CoroutineDispatcher
): ComponentContext by componentContext, KoinComponent {
    // 코루틴 스코프 설정
    private val componentScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val menuRepos: MenuReposImpl by inject()

    private var _menuName = MutableStateFlow("")
    val menuName = _menuName.asStateFlow()

    private var _menuOrder = MutableStateFlow("")
    val menuOrder = _menuOrder.asStateFlow()

    private fun onBackButtonClick() {
        rootComponent.pop()
    }

    private fun onSubmitClick() {
        val name = _menuName.value
        val order = _menuOrder.value

        val menu = Menu(
            menuId = null, // ID는 null로 설정하여 자동 생성되도록 함
            menuName = name,
            idx = order.toIntOrNull(), // 메뉴 순서 변환
            isDisabled = 0, // 기본값 설정
            regDate = null // 날짜는 서버에서 설정할 수 있도록 null로 설정
        )

        componentScope.launch {
            menuRepos.insertMenu(menu)
                .onEach { apiResult ->
                    when (apiResult) {
                        is ApiResult.Success -> {
                            println("Settings Submitted: Menu Name = ${_menuName.value}, Menu Order = ${_menuOrder.value}")
                        }

                        is ApiResult.Error -> {
                            println("Error in insertMenu")
                        }

                        is ApiResult.Loading -> {
                            println("Loading in insertMenu")
                        }
                    }
                }.launchIn(this)
        }
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
            onMenuOrderChange = { _menuOrder.value = it },
            onSubmitClick = { onSubmitClick() }
        )
    }
}