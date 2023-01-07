package ch.skyfy.ghuperms.api

import ch.skyfy.ghuperms.api.db.DatabaseManager
import ch.skyfy.ghuperms.api.utils.setupConfigDirectory
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.nio.file.Path

@Suppress("MemberVisibilityCanBePrivate")
class GhuPermsApiMod : DedicatedServerModInitializer {

    companion object {
        const val MOD_ID: String = "ghupermsapi"
        val CONFIG_DIRECTORY: Path = FabricLoader.getInstance().configDir.resolve(MOD_ID)
        val LOGGER: Logger = LogManager.getLogger(GhuPermsApiMod::class.java)
    }

    init {
        setupConfigDirectory()
        DatabaseManager
    }

    override fun onInitializeServer() {

    }




}