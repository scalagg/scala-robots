package gg.scala.plugin.robots.bot.behavior.actions

import gg.scala.plugin.robots.ScalaRobotsPlugin
import gg.scala.plugin.robots.bot.behavior.ActionBehavior
import gg.scala.plugin.robots.bots.Bot
import me.lucko.helper.terminable.composite.CompositeTerminable
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * @author Topu
 * @date 8/14/2024
 */
object BuildingAction : ActionBehavior
{
    val materials = listOf(
        Material.STONE,
        Material.WOOD,
        Material.WOOL
    )

    override fun initialize(terminable: CompositeTerminable, bot: Bot)
    {
        val blocks = getBlocks(bot)

        if (blocks.isEmpty()) {
            terminable.closeAndReportException()
            return
        }

        val switchSlow = bot.inventoryManager.switchSlow(blocks.first())
        ScalaRobotsPlugin.instance!!.featureManager.buildingFeature.build(bot, terminable, switchSlow)
    }

    override fun tick(terminable: CompositeTerminable, bot: Bot)
    {

    }

    override fun complete(bot: Bot)
    {

    }

    fun getBlocks(bot: Bot) : List<ItemStack> {
        var blocks = bot.inventoryManager.hasItemUsingName(materials[0])
        for (material in materials) {
            if (bot.inventoryManager.hasItemUsingName(material).isEmpty()) continue
            blocks = bot.inventoryManager.hasItemUsingName(material)
            break
        }

        return blocks
    }
}
