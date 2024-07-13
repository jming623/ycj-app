package com.yuventius.sample_project.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.ktorm.database.Database
import org.ktorm.logging.ConsoleLogger
import org.ktorm.logging.LogLevel
import org.ktorm.support.mysql.MySqlDialect

object DatabaseConfig {
    val database = Database.connect(
        dataSource = HikariDataSource(
            /**
             * FIXME
             * DB url 수정 시 [System.getenv("DB_URL")] 을 변경할 것
             * * EX) jdbc:mariadb://localhost:3306/{Database Name}
             */
            HikariConfig().apply {
                jdbcUrl = System.getenv("DB_URL")
                driverClassName = "org.mariadb.jdbc.Driver"
                username = "test"
                password = "test"
            }
        ),
        dialect = MySqlDialect(),
        logger = ConsoleLogger(threshold = LogLevel.INFO),
        alwaysQuoteIdentifiers = true,
        generateSqlInUpperCase = false
    )
}