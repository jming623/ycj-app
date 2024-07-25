package com.jetbrains.ycjapp.dao

import org.ktorm.entity.Entity


interface Menu: Entity<Menu> {
    val menuId: Int
    val menuName: String
    val idx: Int
    val isDisabled: Int
    val regDate: String
}

