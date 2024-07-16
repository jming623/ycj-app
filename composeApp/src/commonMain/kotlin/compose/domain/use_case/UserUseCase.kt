package compose.domain.use_case

import compose.domain.repos.UserRepos
import compose.util.Apis

class UserUseCase(
    private val userRepos: UserRepos
) {
    fun getUsers(nickname: String? = null, searchType: Apis.User.SearchType = Apis.User.SearchType.DSL) = userRepos.getUser(nickname, searchType)
}