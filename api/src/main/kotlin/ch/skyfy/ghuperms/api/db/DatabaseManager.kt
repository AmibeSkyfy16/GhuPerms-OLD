package ch.skyfy.ghuperms.api.db

import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import java.nio.file.Paths
import kotlin.io.path.inputStream

 val Database.permissions get() = this.sequenceOf(Permissions)
 val Database.groups get() = this.sequenceOf(Groups)
 val Database.groupPermissions get() = this.sequenceOf(GroupPermissions)
 val Database.players get() = this.sequenceOf(Players)
 val Database.playerGroups get() = this.sequenceOf(PlayerGroups)
 val Database.playerPermissions get() = this.sequenceOf(PlayerPermissions)

object DatabaseManager {

    val db: Database

    init {
//        val (url, user, password) = Configs.DB_CONFIG.serializableData
        val url = "jdbc:mariadb://localhost:3307"
        val user = "root"
        val password = "Pa$\$w0rd"
        createDatabase() // Create a new database called TinyEconomyRenewed (if it is not already exist)
        db = Database.connect("$url/GhuPerms", "org.mariadb.jdbc.Driver", user, password) // Connect to it
        initDatabase() // Then create tables and populate it with data
    }

    @Suppress("SqlNoDataSourceInspection", "SqlDialectInspection")
    private fun createDatabase() {
//        val (url, user, password) = Configs.DB_CONFIG.serializableData
        val url = "jdbc:mariadb://localhost:3307"
        val user = "root"
        val password = "Pa$\$w0rd"
        Database.connect(url, "org.mariadb.jdbc.Driver", user, password).useConnection { conn ->
            val sql = "create database if not exists `GhuPerms`;"
            conn.prepareStatement(sql).use { statement -> statement.executeQuery() }
        }

    }

    private fun initDatabase() {
//        GhuPermsMod.LOGGER.info("Initializing database with init.sql script \uD83D\uDCC3")
//        val stream = FabricLoader.getInstance().getModContainer(GhuPermsMod.MOD_ID).get().findPath("assets/ghuperms/sql/init.sql").get().inputStream()
        val stream = Paths.get("E:\\tmp\\coding\\GhuPerms\\src\\main\\resources\\assets\\ghuperms\\sql\\init.sql").inputStream()
        db.useConnection { connection ->
            connection.createStatement().use { statement ->
                stream.bufferedReader().use { reader ->
                    for (sql in reader.readText().split(';'))
                        if (sql.any { it.isLetterOrDigit() }) statement.executeUpdate(sql)
                }
            }
        }
    }



}