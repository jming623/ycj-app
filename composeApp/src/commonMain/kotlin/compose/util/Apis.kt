package compose.util

sealed class Apis {
    data object User: Apis() {
        val GET_USERS_BY_DSL: String = "/dsl"
        val GET_USERS_BY_SEQUENCE: String = AppConst.getUrl("/sequence")
        val GET_USERS_BY_NATIVE_QUERY: String = AppConst.getUrl("/nativeQuery")

        enum class SearchType {
            DSL,
            SEQUENCE,
            NATIVE_QUERY
        }
    }
}