package compose.domain.repos

import compose.domain.model.ApiResult
import compose.util.Apis
import data.User
import kotlinx.coroutines.flow.Flow

interface UserRepos {
    fun getUser(
        nickname: String?,
        searchType: Apis.User.SearchType = Apis.User.SearchType.DSL
    ): Flow<ApiResult<List<User>>>
}