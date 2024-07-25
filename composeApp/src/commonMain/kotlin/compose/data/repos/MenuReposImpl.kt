package compose.data.repos

import compose.domain.ApiResult
import compose.util.Apis
import data.Menu
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MenuReposImpl(
    private val httpClient: HttpClient
): MenuRepos {

    override fun getMenu(): Flow<ApiResult<List<Menu>>> = flow{
        emit(ApiResult.Loading())
        try {
            emit(
                ApiResult.Success(
                    httpClient.get(Apis.Menu.GET_MENUS){}.body()
                )
            )
        } catch (e: Exception) {
            println("Error occurred: ${e.message}") // 디버깅용
            emit(ApiResult.Error("Communication Error occurred!"))
        }
    }
}