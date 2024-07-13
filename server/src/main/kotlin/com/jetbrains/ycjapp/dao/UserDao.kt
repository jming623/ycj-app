package com.yuventius.sample_project.dao

import data.User
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.text
import org.ktorm.schema.varchar

object UserTable: Table<IUser>(tableName = "user") {
    val idx = long("idx").primaryKey().bindTo { it.idx }
    val name = varchar("name").bindTo { it.name }
    val gender = varchar("gender").bindTo { it.gender }
}

interface IUser: Entity<IUser> {
    val idx: Long
    val name: String
    val gender: String
}