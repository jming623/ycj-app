package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BottomMenu (
    @SerialName("menu_id")
    val menuId: Int,
    @SerialName("menu_name")
    val menuName: String?,
    @SerialName("idx")
    val idx: Int?,
    @SerialName("is_disabled")
    val isDisabled: Int?,
    @SerialName("reg_date")
    val regDate: String?
)