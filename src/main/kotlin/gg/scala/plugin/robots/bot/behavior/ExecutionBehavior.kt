package gg.scala.plugin.robots.bot.behavior

/**
 * @author GrowlyX
 * @since 8/13/2024
 */
enum class ExecutionBehavior
{
    Queue, // Add to queue
    Override, // Override queue
    OverrideType, // Override only the queues of the same type
    Block; // Block if there is a queue of this type already
}
