package gg.scala.plugin.robots.bot.behavior

import gg.scala.plugin.robots.bots.Bot
import me.lucko.helper.terminable.composite.CompositeTerminable

/**
 * @author GrowlyX
 * @since 8/13/2024
 */
interface ActionBehavior
{
    fun initialize(terminable: CompositeTerminable, bot: Bot)
    fun tick(terminable: CompositeTerminable, bot: Bot)

    fun complete(bot: Bot)
}
