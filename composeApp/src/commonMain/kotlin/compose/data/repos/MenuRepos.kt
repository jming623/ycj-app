package compose.data.repos

import compose.domain.ApiResult
import data.Menu
import kotlinx.coroutines.flow.Flow

interface MenuRepos {
    fun getMenu(): Flow<ApiResult<List<Menu>>>
}