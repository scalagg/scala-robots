package gg.scala.plugin.robots.integration.practice

import gg.scala.plugin.robots.bot.profile.RobotProfileService
import gg.scala.plugin.robots.bot.profile.UnrankedBotsService
import gg.scala.plugin.robots.bot.setting.BotSettingService
import gg.scala.plugin.robots.bot.setting.setting.persistence.SettingProfile
import java.util.UUID

/**
 * @author GrowlyX
 * @since 7/20/2024
 */
fun loadSettingsFromSelectionSync(player: UUID, selection: String, profileChoice: String?): SettingProfile
{
    if (selection.equals("unranked", ignoreCase = true)) {
        val b = UnrankedBotsService.bots.values.random().settingProfile
        return b
    }

    if (runCatching { UUID.fromString(selection) }.getOrNull() != null)
    {
        val profile = RobotProfileService.find(player)
            ?: RobotProfileService.loadCopyOf(player).join()
            ?: return BotSettingService.loadDefaultProfile("EASY")

        return profile.profiles[profileChoice ?: "default"]
            ?: BotSettingService.loadDefaultProfile("EASY")
    }

    return BotSettingService.loadDefaultProfile(selection)
}
