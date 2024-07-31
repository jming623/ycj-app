package com.jetbrains.ycjapp.service

import com.jetbrains.ycjapp.config.DatabaseConfig
import data.BottomMenu
import data.Menu
import io.ktor.util.logging.KtorSimpleLogger
import org.ktorm.database.asIterable
import java.sql.SQLException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

    fun insertMenu(menu: Menu): Boolean {
        val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val query = "INSERT INTO menu_tbl (menu_name, idx, is_disabled, reg_date) VALUES (?, ?, ?, ?)"
        logger.info("Attempting to insert menu: Name = ${menu.menuName}, Index = ${menu.idx}, Is Disabled = ${menu.isDisabled}, Date = $now")
        return try {
            db.useConnection { connection ->
                connection.prepareStatement(query).use { statement ->
                    statement.setString(1, menu.menuName)
                    statement.setInt(2, menu.idx ?: 0)
                    statement.setInt(3, menu.isDisabled ?: 0)
                    statement.setString(4, now)
                    val rowsAffected = statement.executeUpdate()
                    rowsAffected > 0 // Return true if at least one row was affected
                }
            }
        } catch (e: SQLException) {
            logger.error("Failed to insert menu: ${e.message}", e)
            false
        }
    }

}