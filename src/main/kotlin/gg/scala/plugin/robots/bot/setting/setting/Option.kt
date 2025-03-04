package gg.scala.plugin.robots.bot.setting.setting

import gg.scala.plugin.robots.bot.menu.profiles.ProfileBackbone
import gg.scala.plugin.robots.bot.setting.setting.persistence.SettingProfile
import gg.scala.plugin.robots.bot.setting.setting.persistence.SettingValue
import gg.scala.plugin.robots.bot.setting.setting.persistence.toInternalValue
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import kotlin.math.ceil

/**
 * @author GrowlyX
 * @since 7/21/2024
 */
data class Option<T : Any>(
    override val id: String,
    override val item: ItemStack,
    override val parent: Category?,
    override val displayName: String,
    val default: T,
    val increment: Double = 1.0,
    val maxValue: Double,
    override val description: List<String> = listOf(),
    override val permission: String? = null
) : SettingLike
{
    constructor(
        display: String,
        material: ItemStack,
        parent: Category?,
        default: T,
        increment: Double,
        maxValue: Double
    ) : this(
        display.lowercase()
            .replace("(", "_")
            .replace(")", "_")
            .replace(" ", "_"),
        material, parent, display, default, increment, maxValue
    )

    constructor(
        id: String,
        display: String,
        material: Material,
        parent: Category?,
        default: T,
        increment: Double,
        maxValue: Double
    ) : this(
        id, ItemStack((material)), parent, display, default, increment, maxValue
    )

    constructor(
        display: String,
        material: Material,
        parent: Category?,
        default: T,
        increment: Double,
        maxValue: Double
    ) : this(
        display.lowercase()
            .replace("(", "_")
            .replace(")", "_")
            .replace(" ", "_"),
        ItemStack((material)), parent, display, default, increment, maxValue
    )

    constructor(
        display: String,
        material: Material,
        parent: Category?,
        default: T,
        maxValue: Double
    ) : this(
        display, material, parent, default, 1.0, maxValue
    )

    constructor(
        display: String,
        material: Material,
        parent: Category?,
        default: T
    ) : this(
        display, material, parent, default, 1.0, -1.0
    )

    fun addDescription(vararg description: String) = copy(description = description.toList())
    fun addDescription(description: List<String>) = copy(description = description)

    fun interact(
        player: Player,
        profile: SettingProfile,
        backbone: ProfileBackbone,
        clickType: ClickType
    )
    {
        val setting = profile.setting(this)
//        fun updateRandomizerRanges()
//        {
//            if (setting.getObject() is Int)
//            {
//                val integer = setting.getObject() as Int
//                profile.updateRandomizerRange(
//                    this,
//                    ((integer - setting.randomizer).coerceAtLeast(0.0)) to ((integer + setting.randomizer).coerceAtMost(
//                        maxValue
//                    ))
//                )
//            } else
//            {
//                val integer = setting.getObject() as Double
//                profile.updateRandomizerRange(
//                    this,
//                    ((integer - setting.randomizer).coerceAtLeast(0.0)) to ((integer + setting.randomizer).coerceAtMost(
//                        maxValue
//                    ))
//                )
//            }
//        }

        when (true)
        {
            (setting.getObject() is Int), (setting.getObject() is Double) ->
            {
                if (clickType == ClickType.SHIFT_LEFT)
                {
                    profile.updateRandomizer(
                        this,
                        (setting.randomizer + increment).coerceAtMost(ceil(maxValue / 2))
                    )

//                    updateRandomizerRanges()
                    backbone.save(profile)
                    return
                }

                if (clickType == ClickType.LEFT)
                {
                    if (setting.getObject() is Int)
                    {
                        val integer = setting.getObject() as Int
                        if (integer + increment.toInt() > maxValue.toInt()) setting.setObject(maxValue.toInt())
                        else setting.setObject(integer + increment.toInt())
                    } else
                    {
                        val integer = setting.getObject() as Double
                        if (integer + increment.toInt() > maxValue) setting.setObject(maxValue)
                        else setting.setObject(integer + increment)
                    }
                    backbone.save(profile)
                    return
                }

                if (clickType == ClickType.RIGHT)
                {
                    if (setting.getObject() is Int)
                    {
                        val integer = setting.getObject() as Int
                        if (integer - increment.toInt() < 0) setting.setObject(0)
                        else setting.setObject(integer - increment.toInt())
                    } else
                    {
                        val integer = setting.getObject() as Double
                        if (integer - increment.toInt() < 0.0) setting.setObject(0.0)
                        else setting.setObject(integer - increment)
                    }
                    backbone.save(profile)
                    return
                }

                if (clickType == ClickType.SHIFT_RIGHT)
                {
                    profile.updateRandomizer(
                        this,
                        setting.randomizer - increment
                    )

//                    updateRandomizerRanges()
                    backbone.save(profile)
                    return
                } else
                {
                    /// ???? INTELLIJ?
                }
            }

            (setting.getObject() is Boolean) ->
            {
                val value = setting.getObject() as Boolean
                setting.setObject(!value)
                backbone.save(profile)
            }

            else ->
            {

            }
        }
    }

    fun composeSettingValue(): SettingValue
    {
        return SettingValue(
            default.toInternalValue(),
            increment
        )
    }

    override fun describe(player: Player, context: SettingProfile): List<String>
    {
        val setting = context.setting(this)
        val existing = super.describe(player, context)
        val newLines = when (true)
        {
            (setting.getObject() is Int), (setting.getObject() is Double) ->
            {
                val value = context.value(this)

                listOf(
                    "",
                    "${CC.SEC}Value: ${CC.WHITE}${
                        when (true)
                        {
                            (value is Double) -> "%.2f".format(value.toFloat())
                            else -> "$value"
                        }
                    }",
                    "${CC.GOLD}Randomizer: ${CC.WHITE}${
                        "%.2f".format(setting.randomizer.toFloat())
                    } ${
//                        "${CC.GRAY}(${CC.WHITE}${
//                            "%.2f".format(setting.randomizerMinMax.first.toFloat())
//                        } ${CC.GREEN}◀▶ ${CC.WHITE}${
//                            "%.2f".format(setting.randomizerMinMax.second.toFloat())
//                        }${CC.GRAY})"
                        setting.getObject().toInternalValue().randomizeRange(setting.randomizer)
                    }",
                    "",
                    "${CC.GREEN}▲ ${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}${
                        "Left Click"
                    }",
                    "${CC.GREEN}   ${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}${
                        "Shift Left Click for Randomizer"
                    }",
                    "${CC.RED}▼ ${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}${
                        "Right Click for Value"
                    }",
                    "${CC.RED}   ${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}${
                        "Shift Right Click for Randomizer"
                    }"
                )
            }

            (setting.getObject() is Boolean) ->
            {
                val value = setting.getObject() as Boolean
                listOf(
                    "",
                    if (value) "&a&l│ &fOn" else "&c│ &fOn",
                    if (value) "&c│ &fOff" else "&a&l│ &fOff",
                    "",
                    "${CC.GREEN}Click to switch!"
                )
            }

            else -> listOf()
        }

        return existing + newLines
    }
}
