package gg.scala.plugin.robots.bot.behavior.actions

import gg.scala.plugin.robots.ScalaRobotsPlugin
import gg.scala.plugin.robots.bot.behavior.ActionBehavior
import gg.scala.plugin.robots.bots.Bot
import me.lucko.helper.terminable.composite.CompositeTerminable
import org.bukkit.Material

/**
 * @author Topu
 * @date 8/14/2024
 */
object WaterPlacementAction : ActionBehavior
{
    override fun initialize(terminable: CompositeTerminable, bot: Bot)
    {
        val buckets = bot.inventoryManager.hasItemUsingName(Material.WATER_BUCKET)

        if (buckets.size < 1) {
            terminable.closeAndReportException()
            return
        }

        val switchSlow = bot.inventoryManager.switchSlow(buckets.first())
        ScalaRobotsPlugin.instance!!.featureManager.waterPlacementFeature.place(bot, terminable, switchSlow)
    }

    override fun tick(terminable: CompositeTerminable, bot: Bot)
    {

    }

    override fun complete(bot: Bot)
    {

    }
}
