package compose.data.repos

import compose.domain.ApiResult
import data.BottomMenu
import data.Menu
import kotlinx.coroutines.flow.Flow

interface MenuRepos {
    fun getMenu(): Flow<ApiResult<List<Menu>>>
    fun getBottomMenu(): Flow<ApiResult<List<BottomMenu>>>
}