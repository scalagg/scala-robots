package gg.scala.plugin.robots.commands

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.plugin.robots.integration.practice.freebotfridays.FreeBotFridays
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.time.TimeUtil

/**
 * @author GrowlyX
 * @since 7/18/2024
 */
@AutoRegister
object FreeBotFridaysCommand : ScalaCommand()
{
    @CommandAlias("freebotfridays|fbf")
    fun onBotCustomizer(player: ScalaPlayer)
    {
        if (FreeBotFridays.isFreeBotFriday())
        {
            player.sendMessage("${CC.GREEN}It is currently Free Bot Friday!")
            return
        }

        player.sendMessage(
            "${CC.B_WHITE}Free Bot Friday:",
            "${CC.WHITE}Join from Friday 3:30PM to Sunday 10PM EST for free access to custom bots!",
            "",
            "${CC.B_WHITE}Next: ${CC.GREEN}${
                TimeUtil.formatIntoAbbreviatedString(
                    FreeBotFridays
                        .timeUntilNextFreeBotFriday()
                        .toSeconds()
                        .toInt()
                )
            }"
        )
    }
}
