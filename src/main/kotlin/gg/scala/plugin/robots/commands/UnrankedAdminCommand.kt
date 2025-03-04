package gg.scala.plugin.robots.commands

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.acf.annotation.Default
import gg.scala.commons.acf.annotation.Subcommand
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.plugin.robots.bot.menu.management.UnrankedBotManagementMenu
import net.evilblock.cubed.util.CC

/**
 * @author Topu
 * @date 8/29/2024
 */
@AutoRegister
@CommandAlias("unrankedadmin|ura")
@CommandPermission("practice.bot.unrankedadmin")
object UnrankedAdminCommand : ScalaCommand()
{
    var available = true

    @Default
    fun onUnrankedAdmin(player: ScalaPlayer) {
        UnrankedBotManagementMenu().openMenu(player.bukkit())
    }

    @Subcommand("toggle")
    fun onUnrankedBotToggle(player: ScalaPlayer) {
        available = !available
        player.sendMessage("${CC.GREEN}Toggled to: $available")
    }
}