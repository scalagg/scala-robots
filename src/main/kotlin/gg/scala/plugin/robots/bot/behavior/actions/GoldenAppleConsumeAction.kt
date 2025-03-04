package gg.scala.plugin.robots.bot.behavior.actions

import gg.scala.plugin.robots.bot.behavior.ActionBehavior
import gg.scala.plugin.robots.bots.Bot
import gg.scala.plugin.robots.bots.events.impl.FinishEatingEvent
import me.lucko.helper.Events
import me.lucko.helper.terminable.composite.CompositeTerminable

/**
 * @author GrowlyX
 * @since 8/14/2024
 */
object GoldenAppleConsumeAction : ActionBehavior
{
    override fun initialize(terminable: CompositeTerminable, bot: Bot)
    {
        if (System.currentTimeMillis() - bot.inventoryManager.gappleDelay < 2000) {
            terminable.closeAndReportException()
            return
        }

        if (bot.inventoryManager.hasItem(322).isEmpty()) {
            terminable.closeAndReportException()
            return
        }

        bot.inventoryManager.allowEating()

        val goldenApples = bot.inventoryManager.hasItem(322)
        if (goldenApples.isNotEmpty())
        {
            // TODO: how many golden apples do we need to eat lol
            bot.inventoryManager.eat(goldenApples.first())

            Events
                .subscribe(FinishEatingEvent::class.java)
                .handler {
                    terminable.closeAndReportException()
                }
                .bindWith(terminable)
            return
        }

        terminable.closeAndReportException()
    }

    override fun tick(terminable: CompositeTerminable, bot: Bot)
    {

    }

    override fun complete(bot: Bot)
    {
        bot.inventoryManager.gappleDelay = System.currentTimeMillis()
    }
}
