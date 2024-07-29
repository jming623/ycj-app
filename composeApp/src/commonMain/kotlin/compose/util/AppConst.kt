package compose.util

object AppConst {
    /**
     * FIXME
     * [BASE_URL]은 ipconfig를 통해 로컬 IP로 수정할 것
     * 추가로 ycj-app/composeApp/src/androidMain/res/xml/network_security_config.xml에 추가할 것
     */
    const val BASE_URL = "http://192.168.168.100:8080"

    fun getUrl(path: String = ""): String = if (path.first() == '/') {
        "$BASE_URL$path"
    } else {
        "$BASE_URL/$path"
    }
}