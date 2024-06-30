package example.com.repository

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import example.com.data.table.NoteTable
import example.com.data.table.UserTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        Database.connect(hikari())
        transaction {
            SchemaUtils.create(UserTable)
            SchemaUtils.create(NoteTable)
        }
    }

    fun hikari(): HikariDataSource {
        val config = HikariConfig().apply {
            jdbcUrl = System.getenv("DATABASE_URL")
            driverClassName = System.getenv("JDBC_DRIVER")
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }
}