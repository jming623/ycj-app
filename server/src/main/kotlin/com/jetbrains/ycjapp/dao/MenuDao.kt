package com.jetbrains.ycjapp.dao

import com.jetbrains.ycjapp.dao.UserTable.primaryKey
import com.yuventius.sample_project.dao.UserTable.bindTo
import com.yuventius.sample_project.dao.UserTable.primaryKey
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.varchar

interface Menu: Entity<Menu> {
    val menuId: Int
    val menuName: String
    val idx: Int
    val isDisabled: Int
    val regDate: String
}
object UserTable: Table<Menu>(tableName = "menu_tbl") {
    val menuId =  int("menu_id").primaryKey().bindTo {it.menuId}
    val menuName= varchar("menu_name").bindTo { it.menuName }
    val idx = int("idx").bindTo { it.idx }
    val isDisabled = int("is_disabled").bindTo { it.isDisabled }
    val regDate= varchar("reg_date").bindTo { it.regDate }
}

