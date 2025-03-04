package gg.scala.plugin.robots.bot.menu.profiles

import gg.scala.plugin.robots.bot.setting.setting.persistence.SettingProfile
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 7/22/2024
 */
class GenericDescribeProfilesMenu(
    private val profiles: () -> List<SettingProfile>,
    private val action: (SettingProfile) -> Unit
) : PaginatedMenu()
{
    init
    {
        placeholder = true
    }

    private fun Int.toRow() = (1..7).map { it + this * 9 }
    override fun getAllPagesButtonSlots() = 1.toRow()
    override fun size(buttons: Map<Int, Button>) = 27

    override fun getAllPagesButtons(player: Player): Map<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()
        for (settings in profiles())
        {
            buttons[buttons.size] = ItemBuilder
                .of(Material.PAPER)
                .name("${CC.B_GREEN}${settings.id}")
                .addToLore(
                    "",
                    "${CC.GREEN}Click to select!"
                )
                .glow()
                .toButton { _, _ ->
                    action(settings)
                }
        }
        return buttons
    }

    override fun getPrePaginatedTitle(player: Player) = "Viewing profiles..."
}
