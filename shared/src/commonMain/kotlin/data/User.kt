package data

import data.User.Gender
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val idx: Long?,
    val name: String?,
    val gender: Gender?
) {
    @Serializable
    enum class Gender {
        MALE,
        FEMALE;

        companion object {
            fun find(name: String) = Gender.entries.first { it.name == name }
        }
    }
}


