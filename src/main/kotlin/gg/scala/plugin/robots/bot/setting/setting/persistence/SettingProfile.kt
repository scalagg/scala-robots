package gg.scala.plugin.robots.bot.setting.setting.persistence

import com.mojang.authlib.properties.Property
import gg.scala.plugin.robots.bot.setting.setting.Option
import gg.scala.plugin.robots.bot.setting.setting.RootAccessor
import gg.scala.plugin.robots.bot.setting.setting.root
import gg.scala.plugin.robots.bot.setting.setting.persistence.toInternalValue
import gg.scala.plugin.robots.bots.Bot
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants

/**
 * @author GrowlyX
 * @since 7/21/2024
 */
data class SettingProfile(
    val id: String,
    val settings: MutableMap<String, SettingValue> = mutableMapOf(),
    var botName: String = "${CC.YELLOW}Scala Bot",
    var skinProperties: Property = Bot.DEFAULT,
    var randomizerEnabled: Boolean? = null
)
{
    init
    {
        settings.apply {
            if (isNotEmpty())
            {
                return@apply
            }

            for (option in root.recursiveOptions())
            {
                settings[option.composeId()] = option.composeSettingValue()
            }
        }
    }

    fun updateRandomizer(option: Option<*>, randomizer: Double)
    {
        val optionID = option.composeId()
        settings.computeIfAbsent(optionID) { option.composeSettingValue() }
        settings[optionID]?.randomizer = randomizer
    }

//    fun updateRandomizerRange(option: Option<*>, newMinMax: Pair<Double, Double>)
//    {
//        val optionID = option.composeId()
//        settings.computeIfAbsent(optionID) { option.composeSettingValue() }
//        settings[optionID]?.randomizerMinMax = newMinMax
//        Bukkit.broadcastMessage("${newMinMax.first} - ${newMinMax.second}")
//    }

    fun <T : Any> update(option: Option<T>, newValue: T)
    {
        val optionID = option.composeId()
        settings.computeIfAbsent(optionID) { option.composeSettingValue() }
        settings[optionID]?.setObject(newValue)
    }

    fun describe() = listOf(
        "${CC.GRAY}CPS: ${CC.WHITE}${
            value(RootAccessor.attackSettings.cps)
        } ${CC.D_GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.GRAY}Reach: ${CC.WHITE}${
            "%.2f".format(value(RootAccessor.attackSettings.reach).toFloat())
        } ${CC.D_GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.GRAY}Ping: ${CC.WHITE}${
            value(RootAccessor.connectionSettings.ping)
        }ms",
        "${CC.GRAY}W-Tap: ${CC.PRI}${
            if (value(RootAccessor.wTapSettings.enabled)) "${CC.D_GREEN}✔" else "${CC.D_RED}✘"
        } ${CC.D_GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.GRAY}Rod: ${CC.PRI}${
            if (value(RootAccessor.rodSettings.enabled)) "${CC.D_GREEN}✔" else "${CC.D_RED}✘"
        } ${CC.D_GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.GRAY}Strafe: ${CC.PRI}${
            if (value(RootAccessor.strafeSettings.enabled)) "${CC.D_GREEN}✔" else "${CC.D_RED}✘"
        }",
    )

    fun deepCopy() = copy(
        settings = settings
            .mapValues { it.value.copy() }
            .toMutableMap()
    )

    fun setting(like: Option<*>) = settings.getOrPut(like.composeId()) { SettingValue(like.default.toInternalValue()) }
    fun <T : Any> value(like: Option<T>) = (setting(like)).getObject() as T
}
