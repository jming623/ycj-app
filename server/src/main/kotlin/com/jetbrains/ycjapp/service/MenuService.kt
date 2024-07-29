package com.jetbrains.ycjapp.service

import com.jetbrains.ycjapp.config.DatabaseConfig
import data.BottomMenu
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
                    val menuId = row.getInt("menu_id")
                    val menuName = row.getString("menu_name")
                    val idx = row.getInt("idx")
                    val isDisabled = row.getInt("is_disabled")
                    val regDate = row.getString("reg_date")

                    Menu(menuId, menuName, idx, isDisabled, regDate)
                }
            }
        }
    }

    fun getBottomMenus(): List<BottomMenu> {
        val query = "SELECT * FROM bottom_menu_tbl WHERE is_disabled = FALSE"
        return db.useConnection { connection ->
            connection.prepareStatement(query).use { statement ->
                statement.executeQuery().asIterable().map { row ->
                    val menuId = row.getInt("menu_id")
                    val menuName = row.getString("menu_name")
                    val idx = row.getInt("idx")
                    val isDisabled = row.getInt("is_disabled")
                    val regDate = row.getString("reg_date")

                    BottomMenu(menuId, menuName, idx, isDisabled, regDate)
                }
            }
        }
    }
}