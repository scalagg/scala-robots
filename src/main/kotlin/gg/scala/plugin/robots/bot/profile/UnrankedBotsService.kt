package gg.scala.plugin.robots.bot.profile

import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.type.DataStoreStorageType
import gg.scala.plugin.robots.bot.setting.setting.persistence.SettingProfile
import gg.scala.plugin.robots.bots.Bot

/**
 * @author Topu
 * @date 8/29/2024
 */
@Service
object UnrankedBotsService
{
    val bots: MutableMap<String, RobotSettingProfile> = mutableMapOf()

    @Configure
    fun configure() {
        DataStoreObjectControllerCache
            .create<RobotSettingProfile>()

        DataStoreObjectControllerCache
            .findNotNull<RobotSettingProfile>()
            .loadAll(DataStoreStorageType.MONGO)
            .thenAccept {
                it.values.forEach { profile ->
                    bots[profile.settingProfile.id] = profile
                }
            }
    }

    fun save(id: String, settings: SettingProfile) {
        bots[id]!!.settingProfile = settings
        bots[id]!!.save()
    }
}