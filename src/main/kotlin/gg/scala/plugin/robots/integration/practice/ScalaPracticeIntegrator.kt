package gg.scala.plugin.robots.integration.practice

import gg.scala.practice.Globals
import gg.scala.practice.games.GameImpl
import gg.scala.practice.games.GameService
import gg.scala.practice.games.event.GameStartEvent
import gg.scala.practice.kit.feature.FeatureFlag
import gg.scala.plugin.robots.ScalaRobotsPlugin.Companion.instance
import gg.scala.plugin.robots.bot.fixInventoryBasedOnRBTeamColor
import gg.scala.plugin.robots.bot.setting.setting.persistence.SettingProfile
import gg.scala.plugin.robots.bots.Bot
import gg.scala.plugin.robots.bots.events.Event
import gg.scala.plugin.robots.bots.events.impl.BotAttackLandEvent
import gg.scala.plugin.robots.bots.events.impl.BotDamageEvent
import gg.scala.plugin.robots.utils.Color
import gg.scala.plugin.robots.utils.objects.EventRunnable
import me.lucko.helper.Events
import me.lucko.helper.Schedulers
import me.lucko.helper.terminable.composite.CompositeTerminable
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PotionSplashEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object ScalaPracticeIntegrator
{
    fun spawn(location: Location, game: GameImpl, settings: SettingProfile): Bot
    {
        if (settings.randomizerEnabled == true)
        {
            for (value in settings.settings.values)
            {
                // enable the proxy randomize method when calling getObject()
                value.shouldLocalRandomize = true
            }
        }

        var name = settings.botName
        if (name.length > 16)
        {
            name = name.substring(0, 16)
        }

        val consume = game.consumedBots.getAndIncrement()
        val bot = Bot(
            Globals.POSSIBLE_PLAYER_BOT_UNIQUE_IDS.getOrNull(consume)
                ?: throw IllegalStateException("A game may not have more than 10 bots!"),
            location,
            Color.code(name),
            game,
            settings,
            game.kit
        )

        val noDamageMatch = game.flagMetaData(FeatureFlag.DoNotTakeDamage, "doDamageTick") == "true"
        if (noDamageMatch)
        {
            Events
                .subscribe(EntityDamageByEntityEvent::class.java)
                .handler { event ->
                    if (event.entity is Player)
                    {
                        val attackGame = GameService
                            .byPlayer(event.entity as Player)
                            ?: return@handler

                        if (attackGame == game)
                        {
                            event.damage = 0.0
                        }
                    }
                }
                .bindWith(game)
        }

        bot.damageManager.isAllowGlobalAttacks = false
        bot.damageManager.isNoDamage = noDamageMatch
        bot.movements.isMove = false

        if (game.flag(FeatureFlag.DeathOnLiquidInteraction))
        {
            bot.movements.isEdgeDetector = true
            bot.movements.collision.waterEnterEvent = Runnable { bot.destroy() }
        }

        if (game.flag(FeatureFlag.DeathBelowYAxis))
        {
            bot.movements.isEdgeDetector = true
        }

        if (game.flag(FeatureFlag.KnockbackProfile)) {
            val metadata = game.flagMetaData(
                FeatureFlag.KnockbackProfile,
                "profile"
            )

            if (metadata == "combo")
            {
                bot.damageManager.isCombo = true
            }
        }

        //Boxing support
        if (game.flag(FeatureFlag.WinWhenNHitsReached))
        {
            val robot = game.robotSide()
            val playerSide = game.humanSide()
            val metadata = game.flagMetaData(FeatureFlag.WinWhenNHitsReached, "hits")!!
                .toInt()

            bot.eventListener = (EventRunnable { event: Event? ->
                if (event is BotDamageEvent)
                {
                    if (event.source != null && event.source.entity != null &&
                        event.source.entity.uniqueID in playerSide.players
                    )
                    {
                        val player = event.source.entity.bukkitEntity as Player
                        val playerCounter = game.counter(player)
                        game.humanSide().gameLifecycleArbitraryObjectiveProgress += 1
                        game.humanSide().playerCombos.compute(player.uniqueId) { _, combo -> (combo ?: 0) + 1 }

                        playerCounter.increment("totalHits")
                        playerCounter.increment("combo")

                        if (playerCounter.valueOf("combo") > playerCounter.valueOf("highestCombo"))
                        {
                            playerCounter.update(
                                "highestCombo",
                                playerCounter.valueOf("combo")
                            )
                        }

                        val dataPlayer = instance.dataManager.getPlayer(player)
                        dataPlayer?.hit()

                        if (game.humanSide().gameLifecycleArbitraryObjectiveProgress >= metadata)
                        {
                            bot.kill()
                        }
                    } else
                    {
                        event.isCanceled = true
                    }
                }

                if (event is BotAttackLandEvent)
                {
                    robot.gameLifecycleArbitraryObjectiveProgress += 1
                    game.humanSide().playerCombos.clear()

                    val dataPlayer = instance!!.dataManager.getPlayer(event.player)
                    dataPlayer?.botHit()

                    if (robot.gameLifecycleArbitraryObjectiveProgress >= metadata)
                    {
                        game.complete(robot, "")
                    }
                }
            })
        }

        val terminable = CompositeTerminable.create()
        terminable.bindWith(bot.terminable)

        Events
            .subscribe(GameStartEvent::class.java)
            .filter { bot.game.expectation == it.game.expectation }
            .handler {
                if (bot.isAlive)
                {
                    bot.movements.isMove = true
                    terminable.closeAndReportException()
                }
            }
            .bindWith(terminable)

        Events
            .subscribe(PotionSplashEvent::class.java)
            .handler {
                val affectedEntity = it.affectedEntities
                    .firstOrNull { affected -> affected == bot.npc.bukkitEntity }
                    ?: return@handler

                val healEffect = it.potion.effects
                    .firstOrNull { effect -> effect.type == PotionEffectType.HEAL }

                if (healEffect != null)
                {
                    val intensity = it.getIntensity(affectedEntity)
                    val amountHealed = (intensity * (4 shl healEffect.amplifier) + 0.5)
                    bot.healthManager.addHealth(amountHealed)
                }

                val harmEffect = it.potion.effects
                    .firstOrNull { effect -> effect.type == PotionEffectType.HARM }

                if (harmEffect != null)
                {
                    val intensity = it.getIntensity(affectedEntity)
                    val amountHarmed = (intensity * (4 shl harmEffect.amplifier) + 0.5)
                    bot.healthManager.removeHealth(amountHarmed)
                    bot.damageManager.playSound()
                    bot.damageManager.damageEffect()
                }

                val poisonEffect = it.potion.effects
                    .firstOrNull { effect -> effect.type == PotionEffectType.POISON }

                if (poisonEffect != null)
                {
                    bot.healthManager.addPoisonTicks(poisonEffect.amplifier)
                }

                val slownessEffect = it.potion.effects
                    .firstOrNull { effect -> effect.type == PotionEffectType.SLOW }

                if (slownessEffect != null)
                {
                    bot.healthManager.addSlownessTicks(slownessEffect.amplifier)
                }

                val remainingEffects = it.potion.effects
                    .filter { effect ->
                        effect.type != PotionEffectType.HARM &&
                            effect.type != PotionEffectType.HEAL
                    }

                if (remainingEffects.isNotEmpty())
                {
                    bot.effectsManager.addEffects(remainingEffects)
                }
            }
            .bindWith(bot.terminable)

        val inventoryManager = bot.inventoryManager
        inventoryManager.applyItemList(game.kit.contents.toList())

        game.kit.armorContents
            .forEachIndexed { index, itemStack ->
                if (itemStack == null)
                {
                    return@forEachIndexed
                }

                inventoryManager.updateArmorContents(itemStack.clone(), index + 1)
            }

        bot.fixInventoryBasedOnRBTeamColor()

        Schedulers
            .sync()
            .runLater({
                bot.effectsManager.addEffects(
                    game.kit
                        .potionEffects
                        .entries
                        .map { pair ->
                            PotionEffect(
                                PotionEffectType.getByName(pair.key),
                                Int.MAX_VALUE,
                                pair.value
                            )
                        }
                )
            }, 1L)
            .bindWith(bot.terminable)
        return bot
    }
}
