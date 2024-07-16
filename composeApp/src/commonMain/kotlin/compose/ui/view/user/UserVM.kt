package compose.ui.view.user

import compose.domain.model.ApiResult
import compose.domain.use_case.UserUseCase
import compose.util.Apis
import data.User
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class UserVM (
    private val userUseCase: UserUseCase,
    private val defaultDispatcher: CoroutineDispatcher
): ViewModel() {
    private val _users = MutableStateFlow<ApiResult<List<User>>>(ApiResult.Loading())
    val users = _users.asStateFlow()

    private val _nickname = MutableStateFlow("")
    val nickname = _nickname.asStateFlow()

    private val _searchType = MutableStateFlow(Apis.User.SearchType.DSL)
    val searchType = _searchType.asStateFlow()

    fun emitNickname(nickname: String?) = viewModelScope.launch {
        _nickname.emit(nickname ?: "")
    }

    fun toggleSearchType() = viewModelScope.launch {
        when (_searchType.value) {
            Apis.User.SearchType.DSL -> _searchType.emit(Apis.User.SearchType.SEQUENCE)
            Apis.User.SearchType.SEQUENCE -> _searchType.emit(Apis.User.SearchType.NATIVE_QUERY)
            Apis.User.SearchType.NATIVE_QUERY -> _searchType.emit(Apis.User.SearchType.DSL)
        }
    }

    fun getUsers() {
        viewModelScope.launch {
            userUseCase.getUsers(_nickname.value, _searchType.value)
                .flowOn(defaultDispatcher)
                .catch {
//                    Timber.e(it)
                    _users.value = ApiResult.Error("Internal Error occurred!")
                }
                .collect {
                    _users.value = it
                }
        }
    }
}