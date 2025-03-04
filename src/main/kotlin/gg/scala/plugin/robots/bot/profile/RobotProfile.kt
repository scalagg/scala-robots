package gg.scala.plugin.robots.bot.profile

import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.plugin.robots.bot.setting.setting.persistence.SettingProfile
import java.util.*

data class RobotProfile(
    override val identifier: UUID
) : IDataStoreObject
{
    val profiles = mutableMapOf(
        "default" to SettingProfile("default")
    )

    fun save() = DataStoreObjectControllerCache
        .findNotNull<RobotProfile>()
        .save(this)
}
