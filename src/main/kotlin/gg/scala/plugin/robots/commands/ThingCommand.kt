package gg.scala.plugin.robots.commands

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.flavor.inject.Inject
import gg.scala.practice.games.GameService
import gg.scala.plugin.robots.ScalaRobotsPlugin
import gg.scala.plugin.robots.bot.impl.ScalaPracticeRobotInstanceImpl

/**
 * @author GrowlyX
 * @since 3/8/2024
 */
@AutoRegister
object ThingCommand : ScalaCommand()
{
    @Inject
    lateinit var plugin: ScalaRobotsPlugin

    @CommandAlias("testingthing")
    @CommandPermission("op")
    fun onReplicationHealth(sender: ScalaPlayer)
    {
        GameService.byPlayer(sender.bukkit())?.robotInstance?.firstOrNull()
            ?.apply {
                plugin.featureManager.pearling.testPearl(
                    (this as ScalaPracticeRobotInstanceImpl).spawnedBot!!
                )
            }
    }
}
