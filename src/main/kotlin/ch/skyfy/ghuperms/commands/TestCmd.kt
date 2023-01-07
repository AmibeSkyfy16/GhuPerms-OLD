package ch.skyfy.ghuperms.commands

import ch.skyfy.ghuperms.api.Permission
import ch.skyfy.ghuperms.api.utils.hasPermission
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

class TestCmd(permission: String) : Permission(permission, Command { context ->
    if(context?.source?.player !is ServerPlayerEntity) return@Command 0
    val player = context.source?.player!!

    println("command executed")
    println("value ${getString(context, "aValue")}")

    val result = hasPermission(player.uuidAsString, player.name.string, permission)
    println("result $result")

    return@Command 0
}) {

    companion object{
        fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
            val command = CommandManager.literal("testcmd").then(argument("aValue", StringArgumentType.string()).executes(TestCmd("ghuperms.commands.testcmd")))
            dispatcher.register(command)
        }
    }

}