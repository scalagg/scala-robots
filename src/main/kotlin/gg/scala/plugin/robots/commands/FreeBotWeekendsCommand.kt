package gg.scala.plugin.robots.commands

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.plugin.robots.integration.practice.freebotweekends.FreeBotWeekends
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.time.TimeUtil

/**
 * @author GrowlyX
 * @since 7/18/2024
 */
@AutoRegister
object FreeBotWeekendsCommand : ScalaCommand()
{
    @CommandAlias("freebotweekends|fbw")
    fun onBotCustomizer(player: ScalaPlayer)
    {
        if (FreeBotWeekends.isFreeBotFriday())
        {
            player.sendMessage("${CC.GREEN}It is currently Free Bot Weekends!")
            return
        }

        player.sendMessage(
            "${CC.B_WHITE}Free Bot Weekends:",
            "${CC.WHITE}Join from Friday 3:30PM to Sunday 7:30PM EST for free access to custom bots!",
            "",
            "${CC.B_WHITE}Next: ${CC.GREEN}${
                TimeUtil.formatIntoAbbreviatedString(
                    FreeBotWeekends
                        .timeUntilNextFreeBotFriday()
                        .toSeconds()
                        .toInt()
                )
            }"
        )
    }
}
