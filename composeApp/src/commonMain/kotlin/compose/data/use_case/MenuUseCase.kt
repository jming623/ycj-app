package compose.data.use_case

import compose.data.repos.MenuReposImpl

class MenuUseCase(
    private val menuRepos: MenuReposImpl
) {
    fun getMenus() = menuRepos.getMenu()
}