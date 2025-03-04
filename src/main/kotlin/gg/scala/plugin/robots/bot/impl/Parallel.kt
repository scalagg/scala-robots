package gg.scala.plugin.robots.bot.impl

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * @author GrowlyX
 * @since 7/23/2024
 */
val asyncEventThreadPool: ScheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors())
