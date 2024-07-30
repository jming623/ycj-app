package compose.navigation

import com.arkivanov.decompose.ComponentContext
import compose.domain.ApiResult
import compose.data.use_case.MenuUseCase
import data.BottomMenu
import data.Menu
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

class MenuComponent(
    private val rootComponent: RootComponent,
    componentContext: ComponentContext,
    private val defaultDispatcher: CoroutineDispatcher,
    private val menuUseCase: MenuUseCase
): ComponentContext by componentContext {

    private var _menus = MutableStateFlow<ApiResult<List<Menu>>>(ApiResult.Loading())
    private var _bottomMenus = MutableStateFlow<ApiResult<List<BottomMenu>>>(ApiResult.Loading())
    val menus = _menus.asStateFlow()
    val bottomMenus = _bottomMenus.asStateFlow()

    // 코루틴 스코프 설정
    private val componentScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private fun onSettingsButtonClick() {
        rootComponent.navigate(RootComponent.Configuration.SettingsView)
    }

    init {
        getMenuItems()
        getBottomMenuItems()
    }

    private fun getMenuItems() {
        componentScope.launch {
            menuUseCase.getMenus()
                .flowOn(defaultDispatcher)
                .catch {
                    _menus.value = ApiResult.Error("Internal Error occurred!")
                }
                .collect {
                    _menus.value = it
                }
        }
    }

    private fun getBottomMenuItems() {
        componentScope.launch {
            menuUseCase.getBottomMenus()
                .flowOn(defaultDispatcher)
                .catch {
                    _bottomMenus.value = ApiResult.Error("Internal Error occurred!")
                }
                .collect {
                    _bottomMenus.value = it
                }
        }
    }

    fun onDestroy() {
        componentScope.cancel()
    }
}
