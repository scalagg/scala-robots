package gg.scala.plugin.robots.bot.setting

import gg.scala.plugin.robots.bot.setting.setting.persistence.SettingProfile

data class BotSettingContainer(
    val map: MutableMap<String, SettingProfile> = mutableMapOf()
)
