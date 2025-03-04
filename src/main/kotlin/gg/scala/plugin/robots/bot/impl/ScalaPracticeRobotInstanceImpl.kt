package gg.scala.plugin.robots.bot.impl

import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.type.DataStoreStorageType
import gg.scala.plugin.robots.bot.profile.RobotSettingProfile
import gg.scala.practice.games.GameImpl
import gg.scala.practice.games.robot.RobotInstance
import gg.scala.plugin.robots.bot.setting.setting.RootAccessor
import gg.scala.plugin.robots.bot.setting.setting.persistence.SettingProfile
import gg.scala.plugin.robots.bots.Bot
import gg.scala.plugin.robots.integration.practice.ScalaPracticeIntegrator
import me.lucko.helper.Schedulers
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.Color
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 7/20/2024
 */
class ScalaPracticeRobotInstanceImpl(
    private val gameImpl: GameImpl,
    var settingProfileOriginal: SettingProfile,
    instanceID: Int
) : RobotInstance()
{
    var spawnedBot: Bot? = null
    private val settingProfile = settingProfileOriginal.deepCopy()

    private var level = "normal"

    init
    {
        if (instanceID > 1)
        {
            settingProfile.botName = "${CC.YELLOW}Minion #$instanceID"
        }

        terminable.with {
            spawnedBot?.destroy()
        }

        Schedulers
            .async()
            .run {
                DataStoreObjectControllerCache
                    .findNotNull<RobotSettingProfile>()
                    .loadAll(DataStoreStorageType.MONGO)
                    .thenAccept {
                        if (it.values.any { profile ->
                            profile.settingProfile.id == settingProfileOriginal.id
                        }) {
                            level = "unranked"
                        }
                    }
            }

    }

    override fun uniqueID() = spawnedBot?.uniqueId ?: throw IllegalStateException("Robot has not spawned in yet")
    override fun scalaID() = spawnedBot?.scalaID ?: throw IllegalStateException("Robot has not spawned in yet")

    override fun name(): String = spawnedBot
        ?.profile
        ?.botName
        ?.let {
            if (it[0] == '&' && !alive())
            {
                val otherComponent = it.substring(2)
                "${it.substring(0..1)}${CC.STRIKE_THROUGH}$otherComponent"
            } else
            {
                it
            }
        }
        ?.let(Color::translate) ?: ""

    override fun level(): String
    {
        return level
    }

    override fun ping() = spawnedBot?.profile?.value(RootAccessor.connectionSettings.ping) ?: 100
    override fun initialSpawn()
    {
        if (spawnedBot == null)
        {
            spawnedBot = ScalaPracticeIntegrator.spawn(
                gameImpl.map
                    .findSpawnLocationMatchingTeam(
                        gameImpl.robotSide().teamIdentifier
                    )!!
                    .toLocation(gameImpl.arenaWorld!!),
                gameImpl,
                settingProfile
            )
        }
    }

    override fun alive() = spawnedBot?.isAlive ?: false

    override fun participantConnected(player: Player)
    {
        spawnedBot?.viewsManager?.addPlayer(player)
    }

    override fun spectatorConnected(player: Player)
    {
        spawnedBot?.viewsManager?.addPlayer(player)
    }

    override fun destroy()
    {
        spawnedBot?.destroy()
    }
}
