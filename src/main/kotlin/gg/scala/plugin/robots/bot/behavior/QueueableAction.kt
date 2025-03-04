package gg.scala.plugin.robots.bot.behavior

import me.lucko.helper.terminable.composite.CompositeTerminable

/**
 * @author GrowlyX
 * @since 8/13/2024
 */
class QueueableAction(
    private val controller: ActionController,
    val initialization: Long = System.currentTimeMillis(),
    val behavior: ActionBehavior,
    val configuration: ActionConfiguration
) : CompositeTerminable by CompositeTerminable.create()
{
    private var closed = false
    override fun isClosed() = closed

    init
    {
        with {
            behavior.complete(controller.bot)
            closed = true
        }
    }
}
