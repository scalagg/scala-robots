package gg.scala.plugin.robots.bot.menu.profiles

import gg.scala.plugin.robots.bot.profile.UnrankedBotsService
import gg.scala.plugin.robots.bot.setting.setting.persistence.SettingProfile

/**
 * @author Topu
 * @date 9/6/2024
 */
class RobotProfileBackbone(
    var id: String,
) : ProfileBackbone
{
    override fun save(settings: SettingProfile)
    {
        UnrankedBotsService.save(id, settings)
    }
}