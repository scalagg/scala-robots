package gg.scala.plugin.robots

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.annotations.container.ContainerDisable
import gg.scala.commons.annotations.container.ContainerEnable
import gg.scala.commons.core.plugin.*
import gg.scala.practice.menu.party.PartyPlayBotsIntegration
import gg.scala.practice.player.hotbar.LobbyHotbarService.buildNewBotFightMenu
import gg.scala.plugin.robots.bot.menu.BotFightMenu
import gg.scala.plugin.robots.bot.menu.PartyBotFightPrepareMenu
import gg.scala.plugin.robots.bot.menu.SelectBotFightMenu
import gg.scala.plugin.robots.bot.menu.profiles.GenericDescribeProfilesMenu
import gg.scala.plugin.robots.bot.setting.BotSettingService
import gg.scala.plugin.robots.bot.setting.setting.RootAccessor.attackSettings
import gg.scala.plugin.robots.bot.setting.setting.persistence.InternalValue
import gg.scala.plugin.robots.bots.BotManager
import gg.scala.plugin.robots.bots.events.EventManager
import gg.scala.plugin.robots.features.FeatureManager
import gg.scala.plugin.robots.integration.pit.ScalaPitSupport
import gg.scala.plugin.robots.integration.practice.ScalaPracticeSupport
import gg.scala.plugin.robots.utils.DataManager
import gg.scala.plugin.robots.utils.GameServer
import gg.scala.plugin.robots.utils.PacketListener
import gg.scala.practice.player.hotbar.LobbyHotbarService.buildBotSelectorMenu
import net.evilblock.cubed.serializers.Serializers.create
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializable
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializer
import org.bukkit.Bukkit

@Plugin(
    name = "scala-robots",
    version = "%remote%/%branch%/%id%"
)
@PluginAuthorComposite(
    PluginAuthor("Scala"),
)
@PluginWebsite("https://scala.gg")
@PluginDependencyComposite(
    PluginDependency("scala-commons"),
    PluginDependency("Lemon"),
    PluginDependency("ScalaPractice", soft = true),
    PluginDependency("scala-pit", soft = true)
)
class ScalaRobotsPlugin : ExtendedScalaPlugin()
{
    companion object
    {
        @JvmStatic
        lateinit var instance: ScalaRobotsPlugin
    }

    lateinit var botManager: BotManager
    lateinit var eventManager: EventManager
    lateinit var featureManager: FeatureManager
    lateinit var dataManager: DataManager

    @ContainerEnable
    fun containerEnable()
    {
        // Plugin startup logic
        instance = this

        create {
            registerTypeAdapter(
                InternalValue::class.java,
                AbstractTypeSerializer<AbstractTypeSerializable>()
            )
        }

        // Pre-initializes the root category
        attackSettings

        if (GameServer.practice() || GameServer.pit())
        {
            botManager = BotManager(this)
            dataManager = DataManager(this)

            if (GameServer.practice() || GameServer.pit())
            {
                Bukkit.getServer().pluginManager.registerEvents(PacketListener(), this)
            }
        }

        eventManager = EventManager()

        if (GameServer.practice())
        {
            featureManager = FeatureManager()
            ScalaPracticeSupport.enable()
        } else if (GameServer.pit())
        {
            featureManager = FeatureManager()
            ScalaPitSupport.enable()
        } else
        {
            PartyPlayBotsIntegration.createBotsWith = { player, players ->
                GenericDescribeProfilesMenu(
                    {
                        BotSettingService
                            .cached()
                            .map
                            .values
                            .toList()
                    },
                    { settingProfile ->
                        PartyBotFightPrepareMenu(players, settingProfile.id).openMenu(player)
                    }
                ).openMenu(player)
            }

            buildNewBotFightMenu = {
                BotFightMenu(it)
            }

            buildBotSelectorMenu = {
                SelectBotFightMenu()
            }
        }
    }

    @ContainerDisable
    fun containerDisable()
    {
        if (GameServer.practice() || GameServer.pit())
        {
            for (bot in botManager.bots.values)
            {
                bot.destroy()
            }
        }
    }
}
