package com.yuventius.sample_project.service

import com.yuventius.sample_project.config.DatabaseConfig
import com.yuventius.sample_project.dao.UserTable
import data.User
import io.ktor.util.logging.*
import org.ktorm.database.asIterable
import org.ktorm.dsl.*
import org.ktorm.entity.map
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList

internal val logger = KtorSimpleLogger(UserService::class.java.simpleName)

object UserService {
    private val db = DatabaseConfig.database

    fun insertUser(name: String, gender: User.Gender) {
        db.insert(UserTable) {
            set(it.name, name)
            set(it.gender, gender.name)
        }
    }

    /**
     * DSL(Domain Specific Language) Query
     */
    fun getUsersByDSL(nameFilter: String? = null): List<User> = db.from(UserTable)
        .select()
        .whereWithConditions { condition ->
            nameFilter?.let { nameFilter ->
                condition += UserTable.name like "%$nameFilter%"
            }
        }.map { row ->
            logger.info("raw data: ${row[UserTable.idx]}, ${row[UserTable.name]}, ${row[UserTable.gender]}")
            User(row[UserTable.idx], row[UserTable.name], row[UserTable.gender]?.let { User.Gender.find(it) })
        }

    /**
     * Sequence API
     */
    fun getUsersBySequence(nameFilter: String? = null): List<User> =
        db.sequenceOf(UserTable).toList()
            .filter {
                nameFilter?.let { nameFilter ->
                    it.name.contains(nameFilter)
                } ?: run {
                    true
                }
            }
            .map {
                User(it.idx, it.name, User.Gender.find(it.gender))
            }

    /**
     * Native Query
     */
    fun getUserByNativeQuery(nameFilter: String? = null): List<User> {
        var query = "SELECT * FROM user"
        nameFilter?.let { nameFilter ->
            query += " WHERE name LIKE '%$nameFilter%'"
        }
        return db.useConnection { connection ->
            connection.prepareStatement(query).use { statement ->
                statement.executeQuery().asIterable().map { row ->
                    User(row.getLong(1), row.getString(2), User.Gender.find(row.getString(3)))
                }
            }
        }
    }
}