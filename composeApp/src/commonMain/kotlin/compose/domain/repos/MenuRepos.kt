package compose.domain.repos

import compose.domain.model.ApiResult
import data.Menu
import kotlinx.coroutines.flow.Flow

interface MenuRepos {
    fun getMenu(): Flow<ApiResult<List<Menu>>>
}