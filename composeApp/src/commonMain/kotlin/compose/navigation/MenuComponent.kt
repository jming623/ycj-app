package compose.navigation

import com.arkivanov.decompose.ComponentContext
import compose.domain.model.ApiResult
import compose.domain.use_case.MenuUseCase
import data.Menu
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

class MenuComponent(
    componentContext: ComponentContext,
    private val defaultDispatcher: CoroutineDispatcher, // 추가
    private val menuUseCase: MenuUseCase // 메뉴 데이터를 가져오는 UseCase 추가
): ComponentContext by componentContext {

    private var _menus = MutableStateFlow<ApiResult<List<Menu>>>(ApiResult.Loading())
    val menus = _menus.asStateFlow()

    // 코루틴 스코프 설정
    private val componentScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    init {
        getMenuItems()
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

    // 클래스가 더 이상 필요하지 않을 때 스코프를 취소합니다.
    fun onDestroy() {
        componentScope.cancel()
    }
}
