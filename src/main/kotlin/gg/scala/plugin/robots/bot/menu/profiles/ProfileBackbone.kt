package gg.scala.plugin.robots.bot.menu.profiles

import gg.scala.plugin.robots.bot.setting.setting.persistence.SettingProfile

/**
 * @author GrowlyX
 * @since 7/22/2024
 */
fun interface ProfileBackbone
{
    fun save(settings: SettingProfile)
}
