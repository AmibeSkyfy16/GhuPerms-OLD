package ch.skyfy.ghuperms.api.utils

import ch.skyfy.ghuperms.api.GhuPermsApiMod
import ch.skyfy.ghuperms.api.config.Configs
import ch.skyfy.ghuperms.api.config.Group
import ch.skyfy.ghuperms.api.config.Player
import ch.skyfy.ghuperms.api.db.*
import ch.skyfy.ghuperms.api.utils.ModUtils.Companion.hasPermission2
import org.ktorm.dsl.*
import kotlin.io.path.createDirectory
import kotlin.io.path.exists

fun setupConfigDirectory() {
    try {
        if (!GhuPermsApiMod.CONFIG_DIRECTORY.exists()) GhuPermsApiMod.CONFIG_DIRECTORY.createDirectory()
    } catch (e: java.lang.Exception) {
        GhuPermsApiMod.LOGGER.fatal("An exception occurred. Could not create the root folder that should contain the configuration files")
        throw RuntimeException(e)
    }
}

/**
 * return a player id with the name of the player followed by '#' and her uuid
 */
fun getPlayerId(player: Player) = player.name + "#" + player.uuid

fun hasPermission(uuid: String, name: String, permission: String): Boolean {
    return hasPermission2(player = Player(uuid, name), permission = permission)
}

/**
 * Check if a player has the permission
 *
 * A player will belong to one or more groups containing permissions.
 * It can also have permissions that are directly assigned to it without going through a group.
 * These are given priority in the permissions check
 *
 *
 */
fun hasPermission(player: Player, permission: String): Boolean {

    val playersMap = Configs.PERMISSION_CONFIG.serializableData.players

    val groupsAsId = playersMap.filter { it.key == getPlayerId(player) }.values.flatMap { it.groups }.toMutableList()
    val groups = getGroupsOfPlayer(groupsAsId)

    // Checking directly assigned permission (priority)
    playersMap.filter { it.key == getPlayerId(player) }.values.flatMap { it.permissions }.forEach {

        if (it.id == permission) {
            println("found a non-group permission, value is: ${it.value}")
            return it.value
        }

        if (it.id.contains('*') && permission.contains(it.id.substringBeforeLast('*').substringBeforeLast('.'))) {
            println("found a non-group permission #2, value is: ${it.value}")
            return it.value
        }
    }

    groups.forEach { group -> return recurseGroup(group, permission) }

    return false
}

data class Perm(val permission: String?, val weight: Int?, val value: Boolean?)

class ModUtils{
    companion object {
        @JvmStatic
        fun hasPermission2(player: Player, permission: String): Boolean {
            // get all permissions For all groups (and child groups) of which the player is a member
            val permsList = mutableListOf<Perm>()
            val g = Groups.aliased("g")
            val gp = GroupPermissions.aliased("gp")
            val p = Permissions.aliased("p")
            val pg = PlayerGroups.aliased("pg")
            val tplayer = Players.aliased("tplayer")

            DatabaseManager.db.from(g)
                .leftJoin(gp, on = g.id eq gp.groupId)
                .leftJoin(p, on = p.id eq gp.permissionId)
                .leftJoin(pg, on = g.id eq pg.groupId)
                .leftJoin(tplayer, on = pg.playerId eq tplayer.id)
                .select(g.weight, gp.permValue, p.identifier, g.identifier, g.parentId)
                .where { (tplayer.name like player.name) }
                .orderBy(g.weight.desc())
                .forEach { row ->
                    permsList.add(Perm(row[p.identifier], row[g.weight], row[gp.permValue]))
                    recursePerms(row[g.parentId], permsList)
                }

            var value = permsList.filter { it.permission == permission }.sortedByDescending { it.weight }.distinct().firstOrNull()?.value
            if (value == null) {
                value = permsList.filter { it.permission != null && it.permission.contains('*') && permission.contains(it.permission.substringBeforeLast('*').substringBeforeLast('.')) }
                    .sortedByDescending { it.weight }.distinct().firstOrNull()?.value
            }

            if(value != null)return value

            permsList.clear()

            // check directly assigned perms

            val playerTable = Players.aliased("playerTable")
            val playerPermissionTable = PlayerPermissions.aliased("playerPermissionTable")
            val permissionTable = Permissions.aliased("permissionTable")

            DatabaseManager.db.from(playerTable)
                .leftJoin(playerPermissionTable, on = playerTable.id eq playerPermissionTable.playerId)
                .leftJoin(permissionTable, on = playerPermissionTable.permissionId eq permissionTable.id)
                .select(playerPermissionTable.permValue, permissionTable.identifier)
                .where { playerTable.uuid like player.uuid }
                .orderBy(playerPermissionTable.permValue.asc())
                .forEach { row ->
                    permsList.add(Perm(row[permissionTable.identifier], 0, row[playerPermissionTable.permValue]))
                    println()
                }

            value = permsList.filter { it.permission == permission }.sortedBy { it.value }.distinct().firstOrNull()?.value
            if (value == null) {
                value = permsList.filter { it.permission != null && it.permission.contains('*') && permission.contains(it.permission.substringBeforeLast('*').substringBeforeLast('.')) }
                    .sortedBy { it.value }.distinct().firstOrNull()?.value
            }

            return value ?: false
        }

        private fun recursePerms(parentId: Int?, permsList: MutableList<Perm>) {
            if (parentId != null) {
                val g2 = Groups.aliased("g2")
                val gp2 = GroupPermissions.aliased("gp2")
                val p2 = Permissions.aliased("p2")
                DatabaseManager.db.from(g2)
                    .leftJoin(gp2, on = g2.id eq gp2.groupId)
                    .leftJoin(p2, on = p2.id eq gp2.permissionId)
                    .select(g2.id, g2.parentId, g2.identifier, g2.weight, gp2.permValue, p2.identifier)
                    .where { g2.id eq parentId }
                    .forEach { row2 ->
                        permsList.add(Perm(row2[p2.identifier], row2[g2.weight], row2[gp2.permValue]))
                        recursePerms(row2[g2.parentId], permsList)
                    }
            }
        }
    }
}


private fun recurseGroup(group: Group, permission: String): Boolean {
    return group.permissions.find { it.id == permission || (it.id.contains('*') && permission.contains(it.id.substringBeforeLast('*').substringBeforeLast('.'))) }?.value
        ?: if (group.parent != null) recurseGroup(getGroupsOfPlayer(mutableListOf(group.parent)).first(), permission) else false
}

fun getGroupsOfPlayer(groupsNameList: MutableList<String>): List<Group> = Configs.PERMISSION_CONFIG.serializableData.group.filter { groupsNameList.contains(it.id) }.sortedBy { it.weight }


