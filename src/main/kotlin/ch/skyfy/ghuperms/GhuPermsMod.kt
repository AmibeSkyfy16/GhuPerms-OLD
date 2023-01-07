package ch.skyfy.ghuperms

import ch.skyfy.ghuperms.commands.GhuPermsCmd
import ch.skyfy.ghuperms.commands.TestCmd
import ch.skyfy.ghuperms.api.utils.setupConfigDirectory
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.nio.file.Path

@Suppress("MemberVisibilityCanBePrivate")
class GhuPermsMod : DedicatedServerModInitializer {

    companion object {
        const val MOD_ID: String = "ghuperms"
        val CONFIG_DIRECTORY: Path = FabricLoader.getInstance().configDir.resolve(MOD_ID)
        val LOGGER: Logger = LogManager.getLogger(GhuPermsMod::class.java)
    }

    init {
        setupConfigDirectory()
    }

    override fun onInitializeServer() {
        registerCommands()
    }

    private fun registerCommands() {
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            TestCmd.register(dispatcher)
            GhuPermsCmd.register(dispatcher)
        }
    }


}