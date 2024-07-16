package compose.data.repos

import compose.domain.model.ApiResult
import compose.domain.repos.UserRepos
import compose.util.Apis
import data.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.parameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserReposImpl(
    private val httpClient: HttpClient
) : UserRepos {
    override fun getUser(
        nickname: String?,
        searchType: Apis.User.SearchType)
            : Flow<ApiResult<List<User>>> = flow {
        emit(ApiResult.Loading())
        try {
            emit(
                ApiResult.Success(
                    httpClient.get(
                        when (searchType) {
                            Apis.User.SearchType.DSL -> Apis.User.GET_USERS_BY_DSL
                            Apis.User.SearchType.SEQUENCE -> Apis.User.GET_USERS_BY_SEQUENCE
                            Apis.User.SearchType.NATIVE_QUERY -> Apis.User.GET_USERS_BY_NATIVE_QUERY
                        }
                    ) {
                        nickname?.let {
                            parameters {
                                append("nickname", it)
                            }
                        }
                    }.body()
                )
            )
        } catch (e: Exception) {
//            Timber.e(e) Timber는 Android전용 로깅 라이브러리로, Multiflatform환경에서는 Napier이나 kotlinx-logging을 사용한다고 함.
            emit(ApiResult.Error("Communication Error occurred!"))
        }
    }
}