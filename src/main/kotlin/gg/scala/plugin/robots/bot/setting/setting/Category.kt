package gg.scala.plugin.robots.bot.setting.setting

import gg.scala.plugin.robots.bot.setting.setting.persistence.SettingProfile
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 7/21/2024
 */
open class Category(
    override val id: String,
    override val description: List<String>,
    override val item: ItemStack,
    override val parent: Category?,
    override val displayName: String,
    override val permission: String?
) : SettingLike
{

    internal val children = mutableMapOf<String, SettingLike>()
    override fun describe(player: Player, context: SettingProfile) = super.describe(player, context) + listOf(
        "",
        "${CC.GREEN}Click to view!"
    )

    override fun getAbstractType() = Category::class.java

    fun addChildren(vararg settingLike: SettingLike)
    {
        settingLike.forEach {
            children[it.composeId()] = it
        }
    }

    fun recursiveOptions(): List<Option<*>>
    {
        val options = children.values
            .filterIsInstance<Option<*>>()
            .toList()

        return options + children.values
            .filterIsInstance<Category>()
            .flatMap { it.recursiveOptions() }
    }

    fun <T> childByType(id: String): T
    {
        return children[id] as T
    }

}
