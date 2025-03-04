package gg.scala.plugin.robots.bot.setting

import gg.scala.commons.persist.datasync.DataSyncKeys
import gg.scala.commons.persist.datasync.DataSyncService
import gg.scala.commons.persist.datasync.DataSyncSource
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Service
import gg.scala.practice.namespace
import gg.scala.practice.namespaceShortened
import gg.scala.practice.suffixWhenDev
import gg.scala.plugin.robots.ScalaRobotsPlugin
import gg.scala.plugin.robots.bot.setting.setting.persistence.SettingProfile
import net.kyori.adventure.key.Key

@Service
object BotSettingService: DataSyncService<BotSettingContainer>()
{
    @Inject lateinit var plugin: ScalaRobotsPlugin

    object BotSettingKeys : DataSyncKeys
    {
        override fun newStore() = "sbb-difficulty-presets"

        override fun store() = Key.key(namespace(), "bot-settings")
        override fun sync() = Key.key(namespaceShortened().suffixWhenDev(), "bsync")
    }

    fun loadDefaultProfile(difficulty: String): SettingProfile
    {
        return cached().map[difficulty] ?: SettingProfile(difficulty)
    }

    override fun postReload()
    {
        if (defaultProfiles.all { cached().map.containsKey(it) })
        {
            return
        }

        val cached = cached()
        defaultProfiles.forEach {
            cached.map.putIfAbsent(it, SettingProfile(it))
        }
        sync(cached)
    }

    override fun locatedIn() = DataSyncSource.Mongo

    override fun keys() = BotSettingKeys
    override fun type() = BotSettingContainer::class.java
}
