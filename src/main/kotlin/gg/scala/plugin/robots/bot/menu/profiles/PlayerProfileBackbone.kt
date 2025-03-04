package gg.scala.plugin.robots.bot.menu.profiles

import gg.scala.plugin.robots.bot.profile.RobotProfileService
import gg.scala.plugin.robots.bot.setting.setting.persistence.SettingProfile
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 7/22/2024
 */
class PlayerProfileBackbone(
    private val player: Player
) : ProfileBackbone
{
    override fun save(settings: SettingProfile)
    {
        val profile = RobotProfileService.find(player)
            ?: return

        profile.profiles[settings.id] = settings
        profile.save()
    }
}
