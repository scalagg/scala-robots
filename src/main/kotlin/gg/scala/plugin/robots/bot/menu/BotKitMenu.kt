package gg.scala.plugin.robots.bot.menu

import gg.scala.practice.kit.Kit
import gg.scala.practice.menu.TemplateKitMenu
import gg.scala.practice.queue.QueueType
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

/**
 * Class created on 7/19/2024

 * @author Max C.
 * @project esta-practice
 * @website https://solo.to/redis
 */
class BotKitMenu(player: Player, private val selectionCallback: (Kit?) -> Unit) : TemplateKitMenu(player, dynamic = true)
{
    init
    {
        updateAfterClick = true
    }

    override fun filterDisplayOfKit(player: Player, kit: Kit) = kit.queueSizes.any { it.second.contains(QueueType.Robot) }

    override fun itemTitleFor(player: Player, kit: Kit) = "${CC.B_GREEN}${kit.displayName}"
    override fun itemDescriptionOf(player: Player, kit: Kit) = listOf(
        "",
        "${CC.GREEN}Click to select!"
    )

    override fun shouldIncludeKitDescription() = false

    override fun itemClicked(player: Player, kit: Kit, type: ClickType)
    {
        Button.playNeutral(player)
        selectionCallback(kit)
    }

    override fun onClose(player: Player, manualClose: Boolean)
    {
        if (manualClose)
        {
            Tasks.sync {
                selectionCallback(null)
            }
        }
    }

    override fun getPrePaginatedTitle(player: Player) = "Select a kit..."
}
