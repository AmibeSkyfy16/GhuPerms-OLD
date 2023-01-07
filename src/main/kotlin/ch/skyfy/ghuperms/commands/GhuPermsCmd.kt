package ch.skyfy.ghuperms.commands

import ch.skyfy.ghuperms.api.Permission
import ch.skyfy.ghuperms.api.config.CommandsAndPermissionsInformation
import ch.skyfy.ghuperms.api.config.Configs
import ch.skyfy.ghuperms.api.db.DatabaseManager
import ch.skyfy.ghuperms.api.db.Group
import ch.skyfy.ghuperms.api.db.groups
import ch.skyfy.jsonconfiglib.updateMap
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType.getInteger
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.CommandNode
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find

class GhuPermsCmd {

    companion object {

        private val map = mutableMapOf<String, String>()

        fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
            val command = CommandManager.literal("testcmd").then(CommandManager.argument("aValue", StringArgumentType.string()).executes(TestCmd("ghuperms.commands.testcmd")))
            dispatcher.register(command)


            val bigCommands = literal("ghuperms")
                .then(
                    literal("create").then(
                        literal("group").then(
                            argument("groupIdentifier", StringArgumentType.string()).then(
                                argument("groupWeight", IntegerArgumentType.integer()).then(
                                    argument("groupDescription", StringArgumentType.string()).executes(CreateGroup("ghuperms.commands.create.group"))
                                )
                            )
                        )
                    )
                )

            dispatcher.register(bigCommands)

            dispatcher.root.children.forEach {
                registerCommandsInConfig(it)
            }

            Configs.COMMANDS_AND_PERMISSIONS_LIST.updateMap(CommandsAndPermissionsInformation::map) { it.putAll(map) }
        }

        private fun registerCommandsInConfig(origin: CommandNode<ServerCommandSource>, children: CommandNode<ServerCommandSource> = origin, deep: Int = 0, sb: StringBuilder = StringBuilder(children.name)): StringBuilder {
            if (deep > 0) sb.append(" " + children.usageText)


            if (children.children.isEmpty()) {

                var perm = ""
                if (children.command is Permission) perm = (children.command as Permission).permission
                map[sb.toString()] = perm

                return sb.clear().append(origin.name)
            }

            if (children.command is Permission) map[sb.toString()] = (children.command as Permission).permission

            children.children.forEach { registerCommandsInConfig(origin, it, deep + 1, sb) }
            return sb
        }

    }

}

class CreateGroup(override val permission: String) : Permission(permission, Command { context ->
    val groupIdentifier = getString(context, "groupIdentifier")
    val groupWeight = getInteger(context, "groupWeight")
    val groupDescription = getString(context, "groupDescription")

    val group = DatabaseManager.db.groups.find { it.identifier eq groupIdentifier }
    if(group == null){
        DatabaseManager.db.groups.add(Group{identifier = groupIdentifier; weight = groupWeight; description = groupDescription})
        println("group added")
    }else{
        println("a group already exist")
    }

    return@Command 0

})
