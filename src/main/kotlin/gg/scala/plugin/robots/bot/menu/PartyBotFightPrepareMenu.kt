package gg.scala.plugin.robots.bot.menu

import com.cryptomorin.xseries.XMaterial
import gg.scala.lemon.LemonConstants
import gg.scala.practice.Globals
import gg.scala.practice.expectation.GameExpectation
import gg.scala.practice.games.bots.BotGameMetadata
import gg.scala.practice.games.bots.storeForUser
import gg.scala.practice.games.team.GameTeam
import gg.scala.practice.games.team.TeamIdentifier
import gg.scala.practice.kit.Kit
import gg.scala.practice.kit.group.KitGroup
import gg.scala.practice.kit.group.KitGroupService
import gg.scala.practice.map.Map
import gg.scala.practice.map.MapService
import gg.scala.practice.menu.TemplateMapMenu
import gg.scala.practice.menu.party.PartyPlayGameSelectMenu
import gg.scala.practice.player.LobbyPlayerService
import gg.scala.practice.queue.QueueService
import gg.scala.practice.queue.QueueType
import gg.scala.practice.region.PlayerRegionFromRedisProxy
import gg.scala.practice.suffixWhenDev
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 8/9/2024
 */
class PartyBotFightPrepareMenu(
    private val players: Set<UUID>,
    private val botTypeNamespace: String,
    private val botTypeID: String? = null,
    private val kit: Kit? = null,
    private val map: Map? = null,
    private var numberOfBots: Int = 1
) : Menu("Preparing robot party match...")
{
    init
    {
        placeholder = true
    }

    override fun onClose(player: Player, manualClose: Boolean)
    {
        if (manualClose)
        {
            Tasks.sync { PartyPlayGameSelectMenu().openMenu(player) }
        }
    }

    override fun size(buttons: kotlin.collections.Map<Int, Button>) = 27
    override fun getButtons(player: Player) = mapOf(
        10 to ItemBuilder
            .copyOf(kit?.displayIcon ?: ItemStack(Material.DIAMOND_SWORD))
            .name("${CC.B_AQUA}Kit")
            .amount(1)
            .addToLore(
                "${CC.GRAY}Selected kit:",
                "${CC.WHITE}${kit?.displayName ?: "${CC.RED}None"}",
                "",
                "${CC.AQUA}Click to select!"
            )
            .toButton { _, _ ->
                Button.playNeutral(player)
                BotKitMenu(player) { kit ->
                    PartyBotFightPrepareMenu(
                        players,
                        botTypeNamespace, botTypeID,
                        kit,
                        null
                        /** new kit, new map. **/
                        ,
                        numberOfBots
                    ).openMenu(player)
                }.openMenu(player)
            },
        11 to ItemBuilder
            .copyOf(map?.displayIcon ?: ItemStack(Material.MAP))
            .amount(1)
            .name("${CC.BD_AQUA}Map")
            .addToLore(
                "${CC.GRAY}Selected map:",
                "${CC.WHITE}${map?.displayName ?: "${CC.RED}None"}",
                "",
                "${CC.D_AQUA}Click to select!"
            )
            .toButton { _, _ ->
                if (!player.hasPermission("bot.custom-map-selection"))
                {
                    Button.playFail(player)
                    player.sendMessage("${CC.RED}You need ${CC.GREEN}Ember${CC.RED} rank to use this feature!")
                    player.sendMessage("${CC.RED}Purchase a rank on our webstore: ${CC.YELLOW}${LemonConstants.STORE_LINK}")
                    return@toButton
                }

                if (kit == null)
                {
                    Button.playFail(player)
                    player.sendMessage("${CC.RED}You must select a kit first!")
                    return@toButton
                }

                val groups = KitGroupService
                    .groupsOf(kit)
                    .map(KitGroup::id)
                    .toSet()

                Button.playNeutral(player)

                val selectionCallback = { map: Map? ->
                    PartyBotFightPrepareMenu(
                        players, botTypeNamespace, botTypeID, kit, map, numberOfBots
                    ).openMenu(player)
                }

                val menu = object : TemplateMapMenu()
                {
                    override fun filterDisplayOfMap(map: Map) = map.associatedKitGroups
                        .intersect(groups)
                        .isNotEmpty()

                    override fun itemDescriptionOf(player: Player, map: Map) = listOf(
                        "",
                        "${CC.GREEN}Click to select!"
                    )

                    override fun itemClicked(player: Player, map: Map, type: ClickType)
                    {
                        Button.playNeutral(player)
                        selectionCallback(map)
                    }

                    override fun getGlobalButtons(player: Player) = mutableMapOf(
                        4 to ItemBuilder
                            .of(Material.NETHER_STAR)
                            .name("${CC.B_AQUA}Random Map")
                            .addToLore(
                                "",
                                "${CC.AQUA}Click to select!"
                            )
                            .toButton { _, _ ->
                                selectionCallback(null)
                            }
                    )

                    override fun getPrePaginatedTitle(player: Player) = "Select a map..."

                    override fun onClose(player: Player, manualClose: Boolean)
                    {
                        if (manualClose)
                        {
                            Tasks.sync {
                                selectionCallback(null)
                            }
                        }
                    }
                }

                menu.openMenu(player)
            },
        12 to ItemBuilder
            .of(Material.ENDER_PORTAL_FRAME)
            .name("${CC.GREEN}Enemy Bots")
            .addToLore(
                "${CC.GRAY}Donators can add multiple bots",
                "${CC.GRAY}to their bot matches!",
                "",
                "${CC.GRAY}${Constants.THIN_VERTICAL_LINE}${CC.GREEN} Ember ${CC.GRAY}${Constants.ARROW_RIGHT} ${CC.WHITE}2",
                "${CC.GRAY}${Constants.THIN_VERTICAL_LINE}${CC.L_PURPLE} Pyro ${CC.GRAY}${Constants.ARROW_RIGHT} ${CC.WHITE}4",
                "${CC.GRAY}${Constants.THIN_VERTICAL_LINE}${CC.YELLOW} Phoenix ${CC.GRAY}${Constants.ARROW_RIGHT} ${CC.WHITE}6",
                "${CC.GRAY}${Constants.THIN_VERTICAL_LINE}${CC.GOLD} Dragon ${CC.GRAY}${Constants.ARROW_RIGHT} ${CC.WHITE}8",
                "",
                "${CC.WHITE}Current: ${CC.GOLD}$numberOfBots",
                "",
                "${CC.GREEN}Left-Click to add!",
                "${CC.GREEN}Right-Click to remove!"
            )
            .toButton { _, type ->
                if (type!!.isRightClick)
                {
                    if (numberOfBots - 1 < 1)
                    {
                        Button.playFail(player)
                        player.sendMessage("${CC.RED}You must have at least 1 bot in your match!")
                        return@toButton
                    }

                    Button.playSuccess(player)
                    numberOfBots -= 1
                    openMenu(player)
                    return@toButton
                }

                if (!player.hasPermission("bot.amount.${numberOfBots + 1}"))
                {
                    Button.playFail(player)
                    player.sendMessage("${CC.RED}You cannot have ${numberOfBots + 1} bots in your match!")
                    return@toButton
                }

                if (numberOfBots + 1 > 10)
                {
                    Button.playFail(player)
                    player.sendMessage("${CC.RED}You may not have more than 10 bots in your match!")
                    return@toButton
                }

                Button.playSuccess(player)
                numberOfBots += 1
                openMenu(player)
            },
        16 to ItemBuilder
            .of(XMaterial.LIME_DYE)
            .name("${CC.GREEN}Play")
            .addToLore("${CC.GRAY}Start your bot match!")
            .apply {
                if (kit == null)
                {
                    addToLore(
                        "",
                        "${CC.RED}You must select a kit!"
                    )
                    return@apply
                }

                addToLore(
                    "",
                    "${CC.GREEN}Click to start!"
                )
            }
            .toButton { _, _ ->
                if (kit == null)
                {
                    return@toButton
                }

                player.closeInventory()
                CompletableFuture.supplyAsync {
                    val instances = mutableSetOf<UUID>()
                    repeat(numberOfBots) {
                        instances += Globals.POSSIBLE_PLAYER_BOT_UNIQUE_IDS[it]
                    }

                    BotGameMetadata(
                        botTypeNamespace, botTypeID, kit.id,
                        map?.name,
                        botInstances = instances
                    ).apply {
                        storeForUser(player.uniqueId)
                    }
                }.thenAccept {
                    val filteredMaps = MapService
                        .cached()
                        .maps
                        .values
                        .filter { map ->
                            !map.locked && map.associatedKitGroups
                                .intersect(
                                    KitGroupService.groupsOf(kit)
                                        .map(KitGroup::id)
                                        .toSet()
                                )
                                .isNotEmpty()
                        }

                    val realMap = map ?: filteredMaps.random()
                    val gameExpectation = GameExpectation(
                        UUID.randomUUID(),
                        players = (players.toList() + it.botInstances.toList()).toMutableSet(),
                        teams = setOf(
                            GameTeam(TeamIdentifier.A, players.toMutableSet()),
                            GameTeam(TeamIdentifier.B, it.botInstances.toMutableSet())
                        ),
                        kitId = kit.id,
                        mapId = realMap.name,
                        queueType = QueueType.Robot,
                        queueId = "party"
                    )

                    player.closeInventory()
                    val lobbyPlayer = LobbyPlayerService.find(player)
                        ?: return@thenAccept run {
                            player.sendMessage("${CC.RED}You are unable to do this right now!")
                        }

                    if (!lobbyPlayer.isInParty())
                    {
                        player.sendMessage("${CC.RED}You are no longer in a party!")
                        return@thenAccept
                    }

                    Button.playNeutral(player)
                    val playerRegion = PlayerRegionFromRedisProxy
                        .of(player)
                        .join()

                    QueueService.createMessage(
                        "create-match",
                        "config" to gameExpectation,
                        "map" to realMap.name,
                        "kit" to kit.id,
                        "region" to playerRegion.name
                    ).publish(
                        channel = "practice:queue".suffixWhenDev()
                    )

                    Button.playSuccess(player)
                    player.sendMessage("${CC.B_GREEN}We're preparing your match...")
                }.exceptionally {
                    it.printStackTrace()
                    player.sendMessage("${CC.RED}We weren't able to create a robot for you. Please report this to an administrator!")
                    return@exceptionally null
                }
            }
    )
}
