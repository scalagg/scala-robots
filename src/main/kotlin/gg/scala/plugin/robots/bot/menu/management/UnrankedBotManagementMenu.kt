package gg.scala.plugin.robots.bot.menu.management

import gg.scala.plugin.robots.bot.menu.profiles.ProfileCategoryMenu
import gg.scala.plugin.robots.bot.menu.profiles.RobotProfileBackbone
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.type.DataStoreStorageType
import gg.scala.plugin.robots.bot.profile.RobotSettingProfile
import gg.scala.plugin.robots.bot.profile.UnrankedBotsService
import gg.scala.plugin.robots.bot.setting.setting.RootAccessor
import gg.scala.plugin.robots.bot.setting.setting.persistence.SettingProfile
import gg.scala.plugin.robots.bot.setting.setting.root
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*

/**
 * @author Topu
 * @date 8/29/2024
 */
class UnrankedBotManagementMenu : PaginatedMenu()
{
    companion object
    {
        @JvmStatic
        val SLOTS = listOf(
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25
        )
    }

    init
    {
        placeholdBorders = true
    }

    override fun getGlobalButtons(player: Player) =
        mutableMapOf<Int, Button>()
            .apply {
                this[4] = ItemBuilder
                    .of(Material.EMERALD)
                    .name("${CC.GREEN}Create Unranked Bot")
                    .addToLore(
                        "${CC.GRAY}Unranked Bot profiles that players",
                        "${CC.GRAY}can queue against and practice with.",
                        "${CC.GRAY}These profiles can earn divisions.",
                        "",
                        "${CC.GREEN}Click to create a new unranked bot"
                    )
                    .toButton { player, _ ->
                        val id = UUID.randomUUID().toString().substring(0, 7)
                        val settings = SettingProfile(id)
                        UnrankedBotsService.bots[id] = RobotSettingProfile(UUID.randomUUID(), settings)
                        UnrankedBotsService.save(id, settings)
                        Button.playSuccess(player!!)
                        openMenu(player)
                    }
            }

    override fun getAllPagesButtons(player: Player) =
        mutableMapOf<Int, Button>()
            .apply {
                var i = 0
                UnrankedBotsService.bots.values.forEach { bot ->
                    this[i++] = ItemBuilder
                        .copyOf(object : TexturedHeadButton(bot.settingProfile.skinProperties.value)
                        {}.getButtonItem(player))
                        .data(3)
                        .name("${CC.PRI}${bot.settingProfile.botName}")
                        .addToLore(
                            "${CC.GRAY}Id: ${CC.WHITE}${bot.settingProfile.id}",
                            "${CC.GRAY}Randomizer Enabled: ${CC.WHITE}${bot.settingProfile.randomizerEnabled ?: "N/A"}",
                            "",
                            "${CC.GRAY}CPS: ${CC.WHITE}${
                                bot.settingProfile.value(RootAccessor.attackSettings.cps)
                            } ${CC.D_GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.GRAY}Reach: ${CC.WHITE}${
                                "%.2f".format(bot.settingProfile.value(RootAccessor.attackSettings.reach).toFloat())
                            } ${CC.D_GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.GRAY}Ping: ${CC.WHITE}${
                                bot.settingProfile.value(RootAccessor.connectionSettings.ping)
                            }ms",
                            "${CC.GRAY}W-Tap: ${CC.PRI}${
                                if (bot.settingProfile.value(RootAccessor.wTapSettings.enabled)) "${CC.D_GREEN}✔" else "${CC.D_RED}✘"
                            } ${CC.D_GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.GRAY}Rod: ${CC.PRI}${
                                if (bot.settingProfile.value(RootAccessor.rodSettings.enabled)) "${CC.D_GREEN}✔" else "${CC.D_RED}✘"
                            } ${CC.D_GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.GRAY}Strafe: ${CC.PRI}${
                                if (bot.settingProfile.value(RootAccessor.strafeSettings.enabled)) "${CC.D_GREEN}✔" else "${CC.D_RED}✘"
                            }",
                            "",
                            "${CC.YELLOW}Left-Click to edit",
                            "${CC.RED}Right-Click to delete"
                        )
                        .toButton { player, clickType ->
                            if (clickType!!.isRightClick)
                            {
                                ConfirmMenu(
                                    "Deletion of ${Constants.DOUBLE_ARROW_RIGHT} ${bot.settingProfile.id} ${Constants.DOUBLE_ARROW_RIGHT} Confirm",
                                    listOf(
                                        "${CC.WHITE}Delete the ${bot.settingProfile.id}'s${CC.WHITE} robot profile."
                                    ),
                                    true
                                ) {
                                    if (it)
                                    {
                                        UnrankedBotsService.bots.remove(bot.settingProfile.id)
                                        Button.playSuccess(player!!)
                                        UnrankedBotManagementMenu().openMenu(player)
                                        DataStoreObjectControllerCache
                                            .findNotNull<RobotSettingProfile>()
                                            .delete(bot.identifier, DataStoreStorageType.MONGO)
                                    } else
                                    {
                                        player!!.sendMessage("${CC.RED}You've cancelled the robot profile removal operation.")
                                    }
                                }.openMenu(player!!)
                                return@toButton
                            }

                            Button.playNeutral(player!!)
                            ProfileCategoryMenu(bot.settingProfile, RobotProfileBackbone(bot.settingProfile.id), root).openMenu(player)
                            return@toButton
                        }
                }
            }

    override fun getAllPagesButtonSlots(): List<Int> = SLOTS

    override fun getPrePaginatedTitle(player: Player) = "Manage Unranked Bots"

    override fun size(buttons: Map<Int, Button>) = 36
    override fun getMaxItemsPerPage(player: Player) = SLOTS.size
}