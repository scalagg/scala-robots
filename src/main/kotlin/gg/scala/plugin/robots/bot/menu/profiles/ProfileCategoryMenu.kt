package gg.scala.plugin.robots.bot.menu.profiles

import gg.scala.lemon.filter.ChatMessageFilterHandler
import gg.scala.plugin.robots.bot.getProfile
import gg.scala.plugin.robots.bot.setting.setting.Category
import gg.scala.plugin.robots.bot.setting.setting.Option
import gg.scala.plugin.robots.bot.setting.setting.persistence.SettingProfile
import gg.scala.plugin.robots.bot.setting.setting.root
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.Color
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture
import kotlin.math.ceil

/**
 * @author GrowlyX
 * @since 7/22/2024
 */
class ProfileCategoryMenu(
    private val profile: SettingProfile,
    private val backbone: ProfileBackbone,
    private val category: Category
) : PaginatedMenu()
{
    init
    {
        placeholder = true
    }

    private fun Int.toRow() = (1..7).map { it + this * 9 }

    override fun getAllPagesButtonSlots() = (1..4).flatMap { it.toRow() }
    override fun getMaxItemsPerPage(player: Player) = getAllPagesButtonSlots().size

    override fun getGlobalButtons(player: Player): Map<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()
        if (category != root)
        {
            return buttons
        }

        buttons[2] = ItemBuilder
            .of(Material.NAME_TAG)
            .name("${CC.B_GOLD}Bot Name")
            .addToLore(
                "${CC.GRAY}Your bot's name is currently:",
                "${CC.WHITE}${profile.botName}",
                "",
                "${CC.GOLD}Click to update!"
            )
            .toButton { _, _ ->
                Button.playNeutral(player)
                player.closeInventory()

                InputPrompt()
                    .withText("${CC.GREEN}Enter a new bot name!")
                    .acceptInput { _, text ->
                        if (ChatMessageFilterHandler.handleMessageFilter(player, text, false, null))
                        {
                            player.sendMessage("${CC.RED}Inappropriate name!")
                            return@acceptInput
                        }

                        profile.botName = Color.translate(text)
                        backbone.save(profile)

                        player.sendMessage("${CC.GREEN}Saved!")
                        ProfileCategoryMenu(profile, backbone, category).openMenu(player)
                    }
                    .start(player)
            }

        buttons[4] = ItemBuilder
            .of(Material.ENDER_PORTAL_FRAME)
            .name("${CC.B_AQUA}Randomizer")
            .addToLore(
                "${CC.WHITE}Enabled: ${
                    if (profile.randomizerEnabled == true) "${CC.GREEN}Yes" else "${CC.RED}No"
                }",
                "",
                "${CC.AQUA}Click to toggle!"
            )
            .toButton { _, _ ->
                Button.playNeutral(player)

                profile.randomizerEnabled = !(profile.randomizerEnabled ?: false)
                backbone.save(profile)

                openMenu(player)
            }

        buttons[6] = ItemBuilder
            .copyOf(
                object : TexturedHeadButton(profile.skinProperties.value) { }
                    .getButtonItem(player)
            )
            .name("${CC.BD_AQUA}Bot Skin")
            .addToLore(
                "",
                "${CC.D_AQUA}Click to update!"
            )
            .data(3)
            .toButton { _, _ ->
                Button.playNeutral(player)
                player.closeInventory()

                InputPrompt()
                    .withText("${CC.GREEN}Enter a new bot skin! ${CC.GRAY}(Must be a valid player!)")
                    .acceptInput { _, text ->
                        player.sendMessage("${CC.GRAY}Loading profile...")
                        CompletableFuture.supplyAsync { getProfile(text) }
                            .thenAccept {
                                if (it == null)
                                {
                                    Button.playFail(player)
                                    player.sendMessage("${CC.RED}Invalid profile!")
                                    ProfileCategoryMenu(profile, backbone, category).openMenu(player)
                                    return@thenAccept
                                }

                                val textures = it.properties.firstOrNull { property -> property.name == "textures" }
                                if (textures == null)
                                {
                                    Button.playFail(player)
                                    player.sendMessage("${CC.RED}Invalid profile!")
                                    ProfileCategoryMenu(profile, backbone, category).openMenu(player)
                                    return@thenAccept
                                }

                                Button.playSuccess(player)
                                profile.skinProperties = textures
                                backbone.save(profile)

                                player.sendMessage("${CC.GREEN}Saved!")
                                ProfileCategoryMenu(profile, backbone, category).openMenu(player)
                            }
                    }
                    .start(player)
            }

        return buttons
    }

    override fun size(buttons: Map<Int, Button>) = 18 + 9 * ceil(category.children.values.size / 7.0).toInt()
    override fun getAllPagesButtons(player: Player): Map<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()
        for (setting in category.children.values)
        {
            buttons[buttons.size] = ItemBuilder
                .copyOf(
                    setting.toItemStack(player, profile)
                )
                .toButton { _, clickType ->
                    if (setting.permission != null)
                    {
                        if (!setting.hasPermission(player))
                        {
                            Button.playFail(player)
                            player.sendMessage("${CC.RED}You do not have permission to use this feature!")
                            return@toButton
                        }
                    }

                    if (setting is Category)
                    {
                        Button.playNeutral(player)
                        ProfileCategoryMenu(profile, backbone, setting).openMenu(player)
                        return@toButton
                    }

                    if (setting is Option<*>)
                    {
                        Button.playSuccess(player)
                        setting.interact(player, profile, backbone, clickType!!)
                        openMenu(player)
                    }
                }
        }

        return buttons
    }

    override fun getPrePaginatedTitle(player: Player) = category.displayName
    override fun onClose(player: Player, manualClose: Boolean)
    {
        if (manualClose)
        {
            if (category.parent == null)
            {
                return
            }

            Tasks.delayed(1L) {
                ProfileCategoryMenu(profile, backbone, category.parent!!).openMenu(player)
            }
        }
    }
}
