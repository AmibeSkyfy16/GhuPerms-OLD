package ch.skyfy.ghuperms.api.config

import ch.skyfy.jsonconfiglib.Defaultable
import ch.skyfy.jsonconfiglib.Validatable
import kotlinx.serialization.Serializable

@Serializable
data class GhuPermsConfig(
    val opPlayerCanUseAllCommands: Boolean
) :  Validatable

class DefaultGhuPermsConfig : Defaultable<GhuPermsConfig>{
    override fun getDefault() = GhuPermsConfig(true)
}