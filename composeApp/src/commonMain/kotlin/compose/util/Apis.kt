package compose.util

sealed class Apis {
    data object  Menu: Apis() {
        val GET_MENUS: String = AppConst.getUrl("/home-menu")
        val GET_BOTTOM_MENUS: String = AppConst.getUrl("/menu/bottom")
    }
}