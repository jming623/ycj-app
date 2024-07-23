package com.jetbrains.ycjapp.service

import com.yuventius.sample_project.config.DatabaseConfig
import com.yuventius.sample_project.dao.UserTable
import data.Menu
import data.User
import io.ktor.util.logging.KtorSimpleLogger
import org.ktorm.database.asIterable
import org.ktorm.dsl.from
import org.ktorm.dsl.insert
import org.ktorm.dsl.like
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.dsl.whereWithConditions
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList

internal val logger = KtorSimpleLogger(MenuService::class.java.simpleName)

object MenuService {
    private val db = DatabaseConfig.database

    /**
     * Native Query
     */
    fun getMenus(): List<Menu> {
        var query = "SELECT * FROM menu_tbl WHERE is_disabled = FALSE"
        return db.useConnection { connection ->
            connection.prepareStatement(query).use { statement ->
                statement.executeQuery().asIterable().map { row ->
                    Menu(row.getInt(1), row.getString(2), row.getInt(3), row.getInt(4), row.getString(5))
                }
            }
        }
    }
}