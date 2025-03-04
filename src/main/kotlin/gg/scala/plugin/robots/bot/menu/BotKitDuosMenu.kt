package gg.scala.plugin.robots.bot.menu

import gg.scala.practice.Globals
import gg.scala.practice.games.bots.BotGameMetadata
import gg.scala.practice.games.bots.storeForUser
import gg.scala.practice.kit.Kit
import gg.scala.practice.menu.TemplateKitMenu
import gg.scala.practice.queue.QueueService
import gg.scala.practice.queue.QueueType
import gg.scala.practice.queue.queueId
import gg.scala.practice.services.GameManagerService
import gg.scala.plugin.robots.bot.setting.setting.persistence.SettingProfile
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import java.util.concurrent.CompletableFuture

/**
 * Class created on 7/19/2024

 * @author Max C.
 * @project esta-practice
 * @website https://solo.to/redis
 */
class BotKitDuosMenu(player: Player, private val difficultyProfile: SettingProfile) : TemplateKitMenu(player)
{
    init
    {
        autoUpdate = true
        updateAfterClick = true
    }

    override fun filterDisplayOfKit(player: Player, kit: Kit) = kit.queueSizes.any { it.second.contains(QueueType.Robot) }

    /*override fun getItemAmount(player: Player, kit: Kit): Int
    {
        val metadata = GameManagerService.buildQueueIdMetadataTracker(queueId {
            kit(kit)
            queueType(QueueType.Robot)
            teamSize(2)
        })

        return (metadata.inGame + metadata.inQueue).coerceIn(1..64)
    }*/

    override fun itemTitleFor(player: Player, kit: Kit) = "${CC.GREEN}${kit.displayName}"
    override fun itemDescriptionOf(player: Player, kit: Kit): List<String>
    {
        val metadata = GameManagerService.buildQueueIdMetadataTracker(queueId {
            kit(kit)
            queueType(QueueType.Robot)
            teamSize(2)
        })

        return listOf(
            "${CC.GRAY}Playing: ${CC.WHITE}${metadata.inGame} ${CC.D_GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.GRAY}Waiting: ${CC.WHITE}${metadata.inQueue}",
            "",
            "${CC.GREEN}Click to join queue!"
        )
    }

    override fun shouldIncludeKitDescription() = false

    private var locked = false
    override fun itemClicked(player: Player, kit: Kit, type: ClickType)
    {
        if (locked)
        {
            return
        }

        locked = true

        Button.playNeutral(player)
        player.closeInventory()

        CompletableFuture.runAsync {
            BotGameMetadata(
                difficultyProfile.id,
                null,
                kit.id,
                null,
                botInstances = setOf(
                    Globals.POSSIBLE_PLAYER_BOT_UNIQUE_IDS[0],
                    Globals.POSSIBLE_PLAYER_BOT_UNIQUE_IDS[1]
                )
            ).storeForUser(player.uniqueId)
        }.thenAccept {
            QueueService.joinQueue(kit, QueueType.Robot, 2, player)
            Button.playNeutral(player)

            player.sendMessage(
                "${CC.GREEN}Joined the queue for ${CC.PRI}Duos ${QueueType.Robot.name} ${kit.displayName}${CC.GREEN}!"
            )
        }.exceptionally {
            it.printStackTrace()
            player.sendMessage("${CC.RED}We weren't able to create a robot for you. Please report this to an administrator!")
            return@exceptionally null
        }
    }

    override fun onClose(player: Player, manualClose: Boolean)
    {
        if (manualClose)
        {
            Tasks.sync {
                BotFightMenu(player).openMenu(player)
            }
        }
    }

    override fun getPrePaginatedTitle(player: Player) = "Queuing ${CC.D_GRAY}${difficultyProfile.botName}${CC.D_GRAY} Robot Duos..."
}
