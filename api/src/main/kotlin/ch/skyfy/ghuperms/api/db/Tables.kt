@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package ch.skyfy.ghuperms.api.db

import org.ktorm.schema.*

interface Permission : org.ktorm.entity.Entity<Permission> {
    companion object : org.ktorm.entity.Entity.Factory<Permission>()

    val id: Int
    var identifier: String
    var description: String
}

interface Group : org.ktorm.entity.Entity<Group> {
    companion object : org.ktorm.entity.Entity.Factory<Group>()

    val id: Int
    var identifier: String
    var weight: Int
    var description: String
    var parent: Group?
}

interface GroupPermission : org.ktorm.entity.Entity<GroupPermission> {
    companion object : org.ktorm.entity.Entity.Factory<GroupPermission>()

    val id: Int
    val permValue: Boolean
    var group: Group
    var permission: Permission
}

interface Player : org.ktorm.entity.Entity<Player> {
    companion object : org.ktorm.entity.Entity.Factory<Player>()

    val id: Int
    var uuid: String
    var name: String
}

interface PlayerGroup : org.ktorm.entity.Entity<PlayerGroup> {
    companion object : org.ktorm.entity.Entity.Factory<PlayerGroup>()

    val id: Int
    var player: Player
    var group: Group
}

interface PlayerPermission : org.ktorm.entity.Entity<PlayerPermission> {
    companion object : org.ktorm.entity.Entity.Factory<PlayerPermission>()

    val id: Int
    val permValue: Boolean
    var player: Player
    var permission: Permission
}

open class Permissions(alias: String?) : Table<Permission>("permission", alias) {
    companion object : Permissions(null)

    override fun aliased(alias: String) = Permissions(alias)

    val id = int("id").primaryKey().bindTo { it.id }
    val identifier = varchar("identifier").bindTo { it.identifier }
    val description = varchar("description").bindTo { it.description }
}

open class Groups(alias: String?) : Table<Group>("group", alias) {
    companion object : Groups(null)
    override fun aliased(alias: String) = Groups(alias)

    val id = int("id").primaryKey().bindTo { it.id }
    val identifier = varchar("identifier").bindTo { it.identifier }
    val weight = int("weight").bindTo { it.weight }
    val description = varchar("description").bindTo { it.description }
    val parentId = int("parent_id").bindTo { it.parent?.id }
//    val parentId = int("parent_id").references(Groups) { it.parent }

//    val parent: Groups get() = parentId.referenceTable as Groups
}

//object Groups : Table<Group>("group") {
//    val id = int("id").primaryKey().bindTo { it.id }
//    val identifier = varchar("identifier").bindTo { it.identifier }
//    val weight = int("weight").bindTo { it.weight }
//    val description = varchar("description").bindTo { it.description }
//    val parentId = int("parent_id").bindTo { it.parent?.id }
//}

open class GroupPermissions(alias: String?) : Table<GroupPermission>("group_permission", alias) {
    companion object : GroupPermissions(null)

    override fun aliased(alias: String) = GroupPermissions(alias)

    val id = int("id").primaryKey().bindTo { it.id }
    val permValue = boolean("perm_value").bindTo { it.permValue }
    val groupId = int("group_id").references(Groups) { it.group }
    val permissionId = int("permission_id").references(Permissions) { it.permission }

    val group get() = groupId.referenceTable as Groups
    val permission get() = permissionId.referenceTable as Permissions
}

open class Players(alias: String?) : Table<Player>("player", alias) {
    companion object : Players(null)

    override fun aliased(alias: String) = Players(alias)

    val id = int("id").primaryKey().bindTo { it.id }
    val uuid = varchar("uuid").bindTo { it.uuid }
    val name = varchar("name").bindTo { it.name }
}

open class PlayerGroups(alias: String?) : Table<PlayerGroup>("player_group", alias) {
    companion object : PlayerGroups(null)

    override fun aliased(alias: String) = PlayerGroups(alias)

    val id = int("id").primaryKey().bindTo { it.id }
    val playerId = int("player_id").references(Players) { it.player }
    val groupId = int("group_id").references(Groups) { it.group }

    val player get() = playerId.referenceTable as Players
    val group get() = groupId.referenceTable as Groups
}

open class PlayerPermissions(alias: String?) : Table<PlayerPermission>("player_permission", alias) {
    companion object : PlayerPermissions(null)

    override fun aliased(alias: String) = PlayerPermissions(alias)

    val id = int("id").primaryKey().bindTo { it.id }
    val permValue = boolean("perm_value").bindTo { it.permValue }
    val playerId = int("player_id").references(Players) { it.player }
    val permissionId = int("permission_id").references(Permissions) { it.permission }

    val player get() = playerId.referenceTable as Players
    val permission get() = permissionId.referenceTable as Permissions
}