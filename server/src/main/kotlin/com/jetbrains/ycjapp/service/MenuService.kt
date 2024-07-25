package com.jetbrains.ycjapp.service

import com.yuventius.sample_project.config.DatabaseConfig
import data.Menu
import io.ktor.util.logging.KtorSimpleLogger
import org.ktorm.database.asIterable

internal val logger = KtorSimpleLogger(MenuService::class.java.simpleName)

object MenuService {
    private val db = DatabaseConfig.database

    /**
     * Native Query
     */
    fun getMenus(): List<Menu> {
        val query = "SELECT * FROM menu_tbl WHERE is_disabled = FALSE"
        return db.useConnection { connection ->
            connection.prepareStatement(query).use { statement ->
                statement.executeQuery().asIterable().map { row ->
                    val id = row.getInt("id")
                    val name = row.getString("name")
                    val price = row.getInt("price")
                    val categoryId = row.getInt("category_id")
                    val description = row.getString("description")

                    Menu(id, name, price, categoryId, description)
                }
            }
        }
    }
}