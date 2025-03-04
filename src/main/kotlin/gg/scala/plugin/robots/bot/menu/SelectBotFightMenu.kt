package gg.scala.plugin.robots.bot.menu

import gg.scala.plugin.robots.commands.UnrankedAdminCommand
import gg.scala.practice.player.hotbar.LobbyHotbarService.buildNewBotFightMenu
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @author Topu
 * @date 9/7/2024
 */
class SelectBotFightMenu : Menu("Select a robot type...")
{
    init
    {
        placeholder = true
    }

    override fun getButtons(player: Player): Map<Int, Button> =
    mutableMapOf<Int, Button>().apply {
        this[12] = ItemBuilder
            .of(Material.GOLD_SWORD)
            .name("${CC.GOLD}Practice")
            .addToLore(
                "",
                "${CC.B_PRI}Info:",
                "${CC.GRAY}${Constants.THIN_VERTICAL_LINE}${CC.SEC} Practice against a variety",
                "${CC.GRAY}${Constants.THIN_VERTICAL_LINE}${CC.SEC} of Bot Difficulties with",
                "${CC.GRAY}${Constants.THIN_VERTICAL_LINE}${CC.SEC} personalized selections.",
                "",
                "${CC.B_PRI}Difficulties:",
                "${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.GREEN}Easy",
                "${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.YELLOW}Normal",
                "${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.RED}Hard",
                "${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.DARK_RED}Expert",
                "${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.DARK_PURPLE}Nightmare",
                "${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.LIGHT_PURPLE}Clown",
                "${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.GOLD}Martian",
                "${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.AQUA}Custom",
                "",
                "${CC.GREEN}Click to browse!"
            )
            .toButton { player, _ ->
                Button.playNeutral(player!!)

                if (player.hasMetadata("frozen"))
                {
                    player.sendMessage("${CC.RED}You cannot join queues while frozen!")
                    return@toButton
                }

                val menu = buildNewBotFightMenu(player)
                    ?: return@toButton run {
                        player.sendMessage("${CC.RED}This feature is coming soon!")
                    }

                menu.openMenu(player)
            }

        this[14] = ItemBuilder
            .of(Material.IRON_SWORD)
            .name("${CC.YELLOW}Unranked")
            .addToLore(
                "",
                "${CC.B_PRI}Info:",
                "${CC.GRAY}${Constants.THIN_VERTICAL_LINE}${CC.SEC} Practice against a variety",
                "${CC.GRAY}${Constants.THIN_VERTICAL_LINE}${CC.SEC} of Bots which are randomized",
                "${CC.GRAY}${Constants.THIN_VERTICAL_LINE}${CC.SEC} Unlock new divisions and rank up as you win",
                "${CC.GRAY}${Constants.THIN_VERTICAL_LINE}${CC.SEC} and have no loss penalty.",
                "",
                "${CC.GREEN}Click to browse!",
            )
            .toButton { player, _ ->
                Button.playNeutral(player!!)

                if (player.hasMetadata("frozen"))
                {
                    player.sendMessage("${CC.RED}You cannot join queues while frozen!")
                    return@toButton
                }

                if (!UnrankedAdminCommand.available) {
                    player.sendMessage("${CC.RED}This feature is unavailable!")
                    return@toButton
                }

                UnrankedBotFightMenu(player)
            }
    }

    override fun size(buttons: Map<Int, Button>): Int = 27
}