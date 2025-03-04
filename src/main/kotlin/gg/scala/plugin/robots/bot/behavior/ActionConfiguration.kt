package gg.scala.plugin.robots.bot.behavior

import java.time.Duration

/**
 * @author GrowlyX
 * @since 8/13/2024
 */
class ActionConfiguration
{
    var shouldContinueMovement = true

    var executionBehavior = ExecutionBehavior.Queue
    var evictFromQueueAfterDuration: Duration = Duration.ZERO
}
