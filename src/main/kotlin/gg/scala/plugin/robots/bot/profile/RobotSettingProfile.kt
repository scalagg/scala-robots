package gg.scala.plugin.robots.bot.profile

import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.store.storage.type.DataStoreStorageType
import gg.scala.plugin.robots.bot.setting.setting.persistence.SettingProfile
import java.util.*

/**
 * @author Topu
 * @date 9/6/2024
 */
class RobotSettingProfile(
    override val identifier: UUID,
    var settingProfile: SettingProfile,
) : IDataStoreObject
{
    fun save() {
        DataStoreObjectControllerCache
            .findNotNull<RobotSettingProfile>()
            .save(this, DataStoreStorageType.MONGO)
    }
}