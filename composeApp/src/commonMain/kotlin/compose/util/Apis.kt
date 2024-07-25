package compose.util

sealed class Apis {
    data object  Menu: Apis() {
        val GET_MENUS: String = AppConst.getUrl("/home-menu")
    }
}