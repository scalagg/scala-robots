package gg.scala.plugin.robots.bot.menu

import gg.scala.staff.ScalaStaffPlugin
import gg.scala.staff.profile.StaffProfileService
import gg.scala.practice.player.LobbyPlayerService
import gg.scala.practice.player.PlayerState
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 8/10/2024
 */
fun Menu.openMenuProtected(player: Player)
{
    val lobbyPlayer = LobbyPlayerService
        .find(player.uniqueId)
        ?: return

    if (lobbyPlayer.state != PlayerState.Idle)
    {
        player.sendMessage("${CC.RED}You cannot join a queue right now!")
        return
    }

    if (player.hasMetadata("vanished"))
    {
        player.sendMessage("${CC.RED}You are currently in vanish! Use ${CC.B}/vanish${CC.RED} to be able to join a queue.")
        return
    }

    val profile = StaffProfileService.find(player)
    if (profile != null && player.hasPermission(ScalaStaffPlugin.STAFF_NODE))
    {
        if (profile.staffAutoVanishEnabled)
        {
            player.sendMessage("${CC.RED}You currently have AutoVanish enabled! Use ${CC.B}/toggleautovanish${CC.RED} to be able to join a queue.")
            return
        }
    }

    openMenu(player)
}
