package gg.scala.plugin.robots.features.impl

import gg.scala.plugin.robots.bot.behavior.ActionConfiguration
import gg.scala.plugin.robots.bot.behavior.ExecutionBehavior
import gg.scala.plugin.robots.bot.behavior.actions.PearlingAction
import gg.scala.plugin.robots.bot.behavior.actions.WaterPlacementAction
import gg.scala.plugin.robots.bots.Bot
import gg.scala.plugin.robots.features.Feature
import me.lucko.helper.Schedulers
import me.lucko.helper.terminable.Terminable
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

/**
 * @author Topu
 * @date 8/13/2024
 */
class WaterPlacementFeature : _root_ide_package_.gg.scala.plugin.robots.features.Feature()
{
    override fun tickEvent(bot: Bot)
    {
        if (System.currentTimeMillis() - 500 < bot.lastPlacedWater) return

        if (bot.healthManager.fireTicks < 5) return

        val buckets = bot.inventoryManager.hasItemUsingName(Material.WATER_BUCKET)

        if (buckets.size < 1) return

        bot.actionController.runAction(
            WaterPlacementAction
        ) { configuration: ActionConfiguration ->
            configuration.executionBehavior = ExecutionBehavior.Block
            configuration.shouldContinueMovement = false
        }
    }

    fun place(bot: Bot, terminable: Terminable, switchSlow: Consumer<Boolean>) {
        bot.lastPlacedWater = System.currentTimeMillis()

        // val previousSlot = bot.inventoryManager.mainHand

        val block = bot.movements.location.block

        bot.movements.isLockLocation = true
        bot.movements.rotations.rotationLockUntil = System.currentTimeMillis() + 1000L
        bot.movements.rotate(0f, 90f)

        Schedulers
            .sync()
            .runLater({
                // bot.inventoryManager.mainHand = bot.inventoryManager.getItemSlot(buckets[0])
                block.type = Material.WATER
                bot.inventoryManager.setItem(bot.inventoryManager.mainHand, ItemStack(Material.BUCKET))

                Schedulers
                    .sync()
                    .runLater({
                        if (block.type.equals(Material.WATER))
                        {
                            block.type = Material.AIR
                            bot.inventoryManager.setItem(
                                bot.inventoryManager.mainHand,
                                ItemStack(Material.WATER_BUCKET)
                            )
                            // bot.inventoryManager.mainHand = previousSlot
                            bot.movements.isLockLocation = false
                        }

                        switchSlow.accept(true)
                        terminable.closeAndReportException()
                    }, 10L)
                    .bindWith(bot.terminable)
            }, 10L)
            .bindWith(bot.terminable)
    }
}
