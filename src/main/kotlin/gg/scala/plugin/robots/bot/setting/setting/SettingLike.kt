package gg.scala.plugin.robots.bot.setting.setting

import gg.scala.plugin.robots.bot.setting.setting.persistence.SettingProfile
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializable
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 7/21/2024
 */
interface SettingLike : AbstractTypeSerializable
{
    val id: String
    val displayName: String

    val permission: String?
    val description: List<String>
    val item: ItemStack
    val parent: Category?

    fun hasPermission(player: Player): Boolean
    {
        return permission == null || player.hasPermission(permission)
    }

    fun toItemStack(player: Player, context: SettingProfile): ItemStack
    {
        return ItemBuilder
            .copyOf(item)
            .name("${CC.GREEN}$displayName")
            .setLore(describe(player, context))
            .build()
    }

    fun describe(player: Player, context: SettingProfile): List<String>
    {
        return description.map { "${CC.WHITE}$it" }
    }

    fun composeId(): String
    {
        if (parent == null)
        {
            return id
        }

        return "${parent!!.composeId()}.$id"
    }
}
