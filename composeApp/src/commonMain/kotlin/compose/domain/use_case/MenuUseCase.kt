package compose.domain.use_case

import compose.data.repos.MenuReposImpl
import compose.domain.repos.MenuRepos
import compose.util.Apis

class MenuUseCase(
    private val menuRepos: MenuReposImpl
) {
    fun getMenus() = menuRepos.getMenu()
}