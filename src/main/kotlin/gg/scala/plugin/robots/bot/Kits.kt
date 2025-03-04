package gg.scala.plugin.robots.bot

import gg.scala.practice.kit.feature.FeatureFlag
import gg.scala.practice.utilities.toRBTeamSide
import gg.scala.plugin.robots.bots.Bot
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 8/9/2024
 */

fun net.minecraft.server.v1_8_R3.ItemStack.toBukkit() = CraftItemStack.asBukkitCopy(this)

fun Bot.fixInventoryBasedOnRBTeamColor()
{
    val team = game.robotSide()
    if (game.flag(FeatureFlag.RedBlueTeams))
    {
        val teamSide = team.teamIdentifier.toRBTeamSide()
        inventoryManager.contents.indices.forEach { index ->
            val nmsItemStack = inventoryManager.contents[index]
                ?: return@forEach
            val itemStack = nmsItemStack.toBukkit()

            if (itemStack.type == Material.WOOL || itemStack.type == Material.STAINED_CLAY)
            {
                inventoryManager.setItem(
                    index,
                    ItemBuilder
                        .copyOf(itemStack)
                        .data(teamSide.blockColor)
                        .build()
                )
            }
        }

        val newArrayOfContents = arrayOf<ItemStack?>(null, null, null, null)
        inventoryManager.armorContents.indices.forEach { index ->
            val nmsItemStack = inventoryManager.armorContents[index]
                ?: return@forEach
            val itemStack = nmsItemStack.toBukkit()

            if (!itemStack.type.name.contains("LEATHER"))
            {
                newArrayOfContents[index] = itemStack
                return@forEach
            }

            newArrayOfContents[index] = ItemBuilder
                .copyOf(itemStack)
                .color(teamSide.armorColor)
                .build()
        }

        inventoryManager.updateArmorContents(newArrayOfContents)
    }
}
