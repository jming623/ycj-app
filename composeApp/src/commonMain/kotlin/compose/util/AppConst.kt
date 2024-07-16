package compose.util

object AppConst {
    /**
     * FIXME
     * 로컬에서 실행할 경우 [BASE_URL] 수정할 것
     * * EX) http://localhost:8080
     */
    const val BASE_URL = "http://localhost:8080"

    fun getUrl(path: String = ""): String = if (path.first() == '/') {
        "$BASE_URL$path"
    } else {
        "$BASE_URL/$path"
    }
}