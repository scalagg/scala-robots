package gg.scala.plugin.robots.bot.menu

import com.cryptomorin.xseries.XMaterial
import gg.scala.lemon.LemonConstants
import gg.scala.practice.queue.QueueType
import gg.scala.practice.queue.queueId
import gg.scala.practice.services.GameManagerService
import gg.scala.plugin.robots.bot.menu.profiles.PlayerProfileBackbone
import gg.scala.plugin.robots.bot.menu.profiles.ProfileCategoryMenu
import gg.scala.plugin.robots.bot.profile.RobotProfileService
import gg.scala.plugin.robots.bot.setting.BotSettingService
import gg.scala.plugin.robots.bot.setting.setting.persistence.SettingProfile
import gg.scala.plugin.robots.bot.setting.setting.root
import gg.scala.plugin.robots.integration.practice.freebotfridays.FreeBotFridays
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

/**
 * Class created on 7/19/2024

 * @author Max C.
 * @project esta-practice
 * @website https://solo.to/redis
 */
class BotFightMenu(private val viewer: Player) : PaginatedMenu()
{
    init
    {
        autoUpdate = true
    }

    override fun getPrePaginatedTitle(player: Player) = "Fight a bot..."

    private fun Player.buildNewItem(id: String, display: String, data: Short, skinBased: Boolean = false): Button
    {
        val profile = BotSettingService.loadDefaultProfile(id)
        val duosTracker = GameManagerService.buildAggregateMetadataTracker(queueId {
            teamSize(2)
            queueType(QueueType.Robot)
        })

        val solosTracker = GameManagerService.buildAggregateMetadataTracker(queueId {
            teamSize(1)
            queueType(QueueType.Robot)
        })

        return (if (skinBased) ItemBuilder
            .copyOf(object : TexturedHeadButton(profile.skinProperties.value)
            {}.getButtonItem(player))
            .data(3)
        else
            ItemBuilder
                .of(Material.WOOL)
                .data(data)
            ).name(display)
/*            .amount(
                (duosTracker.inGame + duosTracker.inQueue + solosTracker.inQueue + solosTracker.inGame).coerceIn(1..64)
            )*/
            .setLore(profile.describe())
            .addToLore(
                "",
                "${CC.SEC}Duos Queue: ${CC.B_PRI}NEW!",
                "${CC.GRAY}Partner up with another",
                "${CC.GRAY}player against two bots!",
                "",
                "${CC.GRAY}Playing: ${CC.WHITE}${duosTracker.inGame + solosTracker.inGame} ${CC.D_GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.GRAY}Queuing: ${CC.WHITE}${duosTracker.inQueue + solosTracker.inQueue}",
                "",
                "${CC.GREEN}Left-Click to play solo!",
                "${CC.YELLOW}Right-Click to queue for duos!",
            )
            .toButton { _, type ->
                if (type!!.isRightClick)
                {
                    Button.playNeutral(player)
                    BotKitDuosMenu(player, profile).openMenuProtected(player)
                    return@toButton
                }

                Button.playNeutral(player)
                BotFightPrepareMenu(id, null).openMenuProtected(this)
            }
    }

    override fun size(buttons: Map<Int, Button>) = if (shouldExpand()) 63 else 45
    override fun getAllPagesButtons(player: Player): Map<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()
        val profile = RobotProfileService.find(player)
            ?: return mapOf()

        for (settings in profile.profiles.values)
        {
            buttons[buttons.size] = ItemBuilder
                .of(Material.PAPER)
                .name("${CC.B_GREEN}${settings.id}")
                .addToLore(
                    *settings.describe().toTypedArray(),
                    "",
                    "${CC.GREEN}Left-Click to play!",
                    "${CC.YELLOW}Right-Click to edit!",
                    "${CC.RED}Shift-Click to delete!"
                )
                .glow()
                .toButton { _, type ->
                    if (!player.hasPermissionForCustomBots())
                    {
                        Button.playFail(player)
                        player.sendMessage("${CC.RED}You need ${CC.GREEN}Ember${CC.RED} rank to use this feature!")
                        player.sendMessage("${CC.RED}Purchase a rank on our webstore: ${CC.YELLOW}${LemonConstants.STORE_LINK}")
                        return@toButton
                    }

                    if (
                        type == ClickType.SHIFT_RIGHT ||
                        type == ClickType.SHIFT_LEFT
                    )
                    {
                        Button.playNeutral(player)
                        ConfirmMenu(
                            "Deletion of ${Constants.DOUBLE_ARROW_RIGHT} ${settings.id} ${Constants.DOUBLE_ARROW_RIGHT} Confirm",
                            listOf(
                                "${CC.WHITE}Delete the ${settings.id}'s${CC.WHITE} robot preset."
                            ),
                            true
                        ) {
                            if (it) {
                                player.sendMessage("${CC.GREEN}You deleted the robot preset with the ID ${CC.YELLOW}${settings.id}${CC.GREEN}.")
                                Button.playSuccess(player)
                                profile.profiles.remove(settings.id)
                                profile.save()
                            } else {
                                player.sendMessage("${CC.RED}You've cancelled the robot preset removal operation.")
                            }
                        }.openMenu(player)
                        return@toButton
                    }

                    if (type == ClickType.RIGHT)
                    {
                        Button.playNeutral(player)
                        ProfileCategoryMenu(settings, PlayerProfileBackbone(player), root).openMenu(player)
                        return@toButton
                    }

                    Button.playNeutral(player)
                    BotFightPrepareMenu(player.uniqueId.toString(), settings.id).openMenuProtected(player)
                }
        }
        return buttons
    }

    private fun Player.hasPermissionForCustomBots() = player.hasPermission("botgui.use") || FreeBotFridays.isFreeBotFriday()

    override fun getMaxItemsPerPage(player: Player) = getAllPagesButtonSlots().size
    override fun getPageButtonSlots() = 38 to 42
    override fun getAllPagesButtonSlots() = (46..52).toList()

    private fun shouldExpand(): Boolean
    {
        val profile = RobotProfileService.find(viewer)
            ?: return false

        return profile.profiles.isNotEmpty()
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>
    {
        val buttons = mutableMapOf(
            //Tiered difficulty bots
            10 to player.buildNewItem("EASY", "${CC.GREEN}Easy", 5),
            11 to player.buildNewItem("NORMAL", "${CC.YELLOW}Normal", 4),
            12 to player.buildNewItem("HARD", "${CC.RED}Hard", 14),
            13 to player.buildNewItem("EXPERT", "${CC.D_RED}Expert", 14),
            14 to player.buildNewItem("DEVIL", "${CC.DARK_PURPLE}Devil", 10),

            //Special bots
            19 to player.buildNewItem("CLOWN", "${CC.PINK}Clown", 6, true),
            20 to player.buildNewItem("MARTIAN", "${CC.GREEN}Martian", 13, true),
            21 to player.buildNewItem("SAMURAI", "${CC.GOLD}Samurai", 1, true),

            //Media bots
            28 to player.buildNewItem("MARCEL", "${CC.PINK}Marcel", 6, true),
            29 to player.buildNewItem("VERZIDE", "${CC.PINK}Verzide", 6, true),
            30 to player.buildNewItem("IUSEHUZUNI", "${CC.PINK}iusehuzuni", 6, true),
            31 to player.buildNewItem("STRAIGHT", "${CC.PINK}Straight", 6, true),
            32 to player.buildNewItem("SKEPPY", "${CC.PINK}Skeppy", 6, true),
            33 to player.buildNewItem("PENGO", "${CC.PINK}Pengo", 6, true),
            34 to player.buildNewItem("CALVIN", "${CC.PINK}Calvin", 6, true),

            40 to ItemBuilder.of(Material.EMERALD)
                .name("&aCustomized Bots")
                .addToLore(
                    "${CC.GRAY}Play against a customized",
                    "${CC.GRAY}bot through the bot customizer",
                    "${CC.GRAY}system!",
                    "",
                )
                .apply {
                    if (player.hasPermission("botgui.use"))
                    {
                        addToLore(
                            "${CC.GREEN}Click to create custom bot!"
                        )
                        return@apply
                    }

                    if (FreeBotFridays.isFreeBotFriday())
                    {
                        addToLore(
                            "${CC.B_GOLD}${CC.MAGIC}AAA${CC.B_GOLD} IT'S FREE BOT WEEKEND! ${CC.B_GOLD}${CC.MAGIC}AAA",
                            "${CC.GOLD}All players have access to use",
                            "${CC.GOLD}custom bots!",
                            "",
                            "${CC.GREEN}Click to create custom bot!"
                        )
                    } else
                    {
                        addToLore(
                            "${CC.B_WHITE}Free Bot Weekend:",
                            "${CC.WHITE}Come back later for ${CC.GREEN}free${CC.WHITE}",
                            "${CC.WHITE}access to custom bots every",
                            "${CC.WHITE}Friday from ${CC.GOLD}3:30PM${CC.WHITE} to Saturday ${CC.GOLD}10:00PM${CC.WHITE} EST!",
                            "",
                            "${CC.B_WHITE}Next event: ${CC.WHITE}${FreeBotFridays.fancyStatus()}"
                        )
                    }
                }
                .toButton { _, type ->
                    if (!(player.hasPermission("botgui.use") || FreeBotFridays.isFreeBotFriday()))
                    {
                        Button.playFail(player)
                        player.sendMessage("${CC.RED}You need ${CC.GREEN}Ember${CC.RED} rank to use this feature!")
                        player.sendMessage("${CC.RED}Purchase a rank on our webstore: ${CC.YELLOW}${LemonConstants.STORE_LINK}")
                        return@toButton
                    }

                    val robotProfile = RobotProfileService.find(player)
                        ?: return@toButton

                    Button.playNeutral(player)
                    player.closeInventory()

                    InputPrompt()
                        .withText("${CC.GREEN}Enter a profile name!")
                        .acceptInput { _, newName ->
                            val sanitized = newName.lowercase().replace(" ", "_")
                            if (robotProfile.profiles[sanitized] != null)
                            {
                                Button.playFail(player)
                                player.sendMessage("${CC.RED}You already have a profile with that name!")
                                BotFightMenu(player).openMenu(player)
                                return@acceptInput
                            }

                            Button.playSuccess(player)
                            player.sendMessage("${CC.GREEN}Your profile has been created!")

                            robotProfile.profiles[sanitized] = SettingProfile(sanitized)
                            robotProfile.save()

                            BotFightMenu(player).openMenu(player)
                        }
                        .start(player)
                }
        )

        (0..8).forEach {
            buttons[it] = ItemBuilder
                .of(XMaterial.BLACK_STAINED_GLASS_PANE)
                .name(" ")
                .toButton()
        }

        (36 .. 44).forEach {
            if (it == 29 || it == 31)
            {
                return@forEach
            }

            buttons.putIfAbsent(it, ItemBuilder
                .of(XMaterial.BLACK_STAINED_GLASS_PANE)
                .name(" ")
                .toButton())
        }

        // side placeholders
        listOf(17, 26).forEach {
            buttons.putIfAbsent(it, ItemBuilder
                .of(XMaterial.BLACK_STAINED_GLASS_PANE)
                .name(" ")
                .toButton())
        }

        listOf(9, 17).forEach {
            buttons[it] = ItemBuilder
                .of(XMaterial.LIME_STAINED_GLASS_PANE)
                .name("&aNormal")
                .addToLore(
                    "${CC.GRAY}Tiered difficulty bots."
                )
                .toButton()
        }

        listOf(18, 26).forEach {
            buttons[it] = ItemBuilder
                .of(XMaterial.ORANGE_STAINED_GLASS_PANE)
                .name("&6Special")
                .addToLore(
                    "${CC.GRAY}Seasonal, or other special bots."
                )
                .toButton()
        }

        listOf(27, 35).forEach {
            buttons[it] = ItemBuilder
                .of(XMaterial.PINK_STAINED_GLASS_PANE)
                .name("&dMedia")
                .addToLore(
                    "${CC.GRAY}Fight your favorite content creators!."
                )
                .toButton()
        }

        if (shouldExpand())
        {
            (54..63).forEach {
                buttons.putIfAbsent(it, ItemBuilder
                    .of(XMaterial.BLACK_STAINED_GLASS_PANE)
                    .name(" ")
                    .toButton())
            }

            listOf(45, 53).forEach {
                buttons[it] = ItemBuilder
                    .of(XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE)
                    .name("&bCustom ${CC.GRAY}(Page #$page)")
                    .addToLore(
                        "${CC.GRAY}Your own custom bot profiles!"
                    )
                    .toButton()
            }
        }

        return buttons
    }
}
