package gg.scala.plugin.robots.integration.practice

import gg.scala.practice.games.GameState
import gg.scala.practice.games.robot.RobotGameLifecycle.createNewRobotInstances
import gg.scala.practice.games.robot.RobotInstance
import gg.scala.plugin.robots.bot.impl.ScalaPracticeRobotInstanceImpl
import gg.scala.plugin.robots.bots.events.impl.BotDeathEvent
import me.lucko.helper.Events
import java.util.concurrent.atomic.AtomicInteger

object ScalaPracticeSupport
{
    fun enable()
    {
        createNewRobotInstances = context@{ game ->
            val leader = game.humanSide().players.first()
            val settingProfile = loadSettingsFromSelectionSync(
                leader,
                game.botGameMetadata!!.botNamespace,
                game.botGameMetadata!!.botProfileID
            )

            val totalDeaths = AtomicInteger(0)

            Events
                .subscribe(BotDeathEvent::class.java)
                .filter { event ->
                    game.state(GameState.Playing) && game.robotInstance
                        .any { it.uniqueID() == event.bot.uniqueId }
                }
                .handler {
                    if (totalDeaths.incrementAndGet() == game.consumedBots.get())
                    {
                        game.complete(game.humanSide())
                    }
                }
                .bindWith(game)

            if (game.botGameMetadata!!.botInstances.size == 1)
            {
                return@context setOf(ScalaPracticeRobotInstanceImpl(game, settingProfile, 1))
            }

            val instances = mutableSetOf<RobotInstance>()
            repeat(game.botGameMetadata!!.botInstances.size) {
                instances += ScalaPracticeRobotInstanceImpl(game, settingProfile, it + 1)
            }

            return@context instances
        }
    }
}
