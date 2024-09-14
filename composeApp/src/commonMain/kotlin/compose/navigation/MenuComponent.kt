package compose.navigation

import com.arkivanov.decompose.ComponentContext
import compose.domain.ApiResult
import compose.data.use_case.MenuUseCase
import data.BottomMenu
import data.Menu
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    val menus: StateFlow<ApiResult<List<Menu>>> = _menus.asStateFlow()

    private var _bottomMenus = MutableStateFlow<ApiResult<List<BottomMenu>>>(ApiResult.Loading())
    val bottomMenus: StateFlow<ApiResult<List<BottomMenu>>> = _bottomMenus.asStateFlow()

    // 코루틴 스코프 설정
    private val componentScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

//    private fun onSettingsButtonClick() {
//        rootComponent.navigate(RootComponent.Configuration.SettingsView)
//    }

    init {
//        refreshData()
    }

    fun refreshData() {
        fetchMenus()
        fetchBottomMenus()
    }

    private fun fetchMenus() {
        componentScope.launch {
            menuUseCase.getMenus()
                .flowOn(defaultDispatcher)
                .catch { e ->
                    _menus.value = ApiResult.Error("Internal Error occurred: ${e.message}")
                }
                .collect { result ->
                    _menus.value = result
                }
        }
    }
    private fun fetchBottomMenus() {
        componentScope.launch {
            menuUseCase.getBottomMenus()
                .flowOn(defaultDispatcher)
                .catch { e ->
                    _bottomMenus.value = ApiResult.Error("Internal Error occurred: ${e.message}")
                }
                .collect { result ->
                    _bottomMenus.value = result
                }
        }
    }

    fun onDestroy() {
        componentScope.cancel()
    }
}
