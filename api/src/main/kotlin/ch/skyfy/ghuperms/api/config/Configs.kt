package ch.skyfy.ghuperms.api.config

import ch.skyfy.ghuperms.api.GhuPermsApiMod
import ch.skyfy.ghuperms.api.GhuPermsApiMod.Companion.CONFIG_DIRECTORY
import ch.skyfy.jsonconfiglib.ConfigData

object Configs {
    val DB_CONFIG = ConfigData<DatabaseConfig, DefaultDataConfig>(CONFIG_DIRECTORY.resolve("database-config.json"), true)
    val GHUPERMS_CONFIG = ConfigData<GhuPermsConfig, DefaultGhuPermsConfig>(CONFIG_DIRECTORY.resolve("ghuperms-config.json"), true)
    val COMMANDS_AND_PERMISSIONS_LIST = ConfigData.invoke<CommandsAndPermissionsInformation, DefaultCommandsAndPermissionsInformation>(GhuPermsApiMod.CONFIG_DIRECTORY.resolve("commands-permissions-information.json"), true)
    val PERMISSION_CONFIG = ConfigData.invoke<PermissionsConfig, DefaultPermissionsConfig>(GhuPermsApiMod.CONFIG_DIRECTORY.resolve("permission-config.json"), true)
}