package ch.skyfy.ghuperms.api

import ch.skyfy.ghuperms.api.config.Configs
import ch.skyfy.ghuperms.api.utils.hasPermission
import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting

open class Permission(open val permission: String, open val command: Command<ServerCommandSource>) : Command<ServerCommandSource> {
    override fun run(context: CommandContext<ServerCommandSource>?): Int {

        if (context != null && context.source.player is ServerPlayerEntity) {
            val player = context.source.player!!
            if (Configs.GHUPERMS_CONFIG.serializableData.opPlayerCanUseAllCommands && player.hasPermissionLevel(4))
                return command.run(context)

            val result = hasPermission(context.source.player!!.uuidAsString, context.source.player!!.name.string, permission)
            println("result for permission $permission is $result")
            if (!result) {
                println("You dont have the permission to use this command !")
                println("You need the following permission to use this command: $permission")
                context.source.player!!.sendMessage(Text.literal("You dont have the permission to use this command !").setStyle(Style.EMPTY.withColor(Formatting.RED)))
                return 0
            };
        }

        return command.run(context)
    }
}