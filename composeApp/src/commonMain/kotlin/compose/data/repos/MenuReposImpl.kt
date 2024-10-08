package compose.data.repos

import compose.domain.ApiResult
import compose.util.Apis
import data.BottomMenu
import data.Menu
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
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

    override fun getBottomMenu(): Flow<ApiResult<List<BottomMenu>>> = flow {
        emit(ApiResult.Loading())
        try {
            emit(
                ApiResult.Success(
                    httpClient.get(Apis.Menu.GET_BOTTOM_MENUS){}.body()
                )
            )
        } catch (e: Exception) {
            println("Error occurred: ${e.message}") // 디버깅용
            emit(ApiResult.Error("Communication Error occurred!"))
        }
    }

    override fun insertMenu(menu: Menu): Flow<ApiResult<Unit>>  = flow {
        emit(ApiResult.Loading())
        try {
            ApiResult.Success(
                httpClient.post(Apis.Menu.INSERT_MENU) {
                    contentType(ContentType.Application.Json)
                    setBody(menu)
                }
            )
            emit(ApiResult.Success(Unit))
        } catch (e: Exception) {
            println("Error occurred: ${e.message}")
            emit(ApiResult.Error("Failed to insert menu!"))
        }
    }
}