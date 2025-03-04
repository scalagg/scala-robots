package gg.scala.plugin.robots.bot.setting.setting.persistence

import net.evilblock.cubed.serializers.impl.AbstractTypeSerializable
import net.evilblock.cubed.util.CC
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.random.Random

/**
 * DO NOT REFACTOR!
 *
 * @author GrowlyX
 * @since 7/21/2024
 */
sealed interface InternalValue<T : Any> : AbstractTypeSerializable
{
    var internalValue: T
    fun randomize(randomizer: Double): T
    fun randomizeRange(randomizer: Double): String
}

data class BooleanValue(
    override var internalValue: Boolean = false
) : InternalValue<Boolean>
{
    override fun randomize(randomizer: Double) = internalValue
    override fun randomizeRange(randomizer: Double): String
    {
        return ""
    }

    override fun getAbstractType() = BooleanValue::class.java
}

data class DoubleValue(
    override var internalValue: Double = 0.0
) : InternalValue<Double>
{
    override fun randomize(randomizer: Double): Double
    {
        val min = internalValue - randomizer
        val max = internalValue + randomizer
        if (min == max) return internalValue
        val normalizedRange = max(min, 0.0)..max
        return Random.nextDouble(normalizedRange.start, normalizedRange.endInclusive)
    }

    override fun randomizeRange(randomizer: Double): String
    {
        return "${CC.GRAY}(${CC.WHITE}${
            "%.2f".format(internalValue - randomizer)
        } ${CC.GREEN}◀▶ ${CC.WHITE}${
            "%.2f".format(internalValue + randomizer)
        }${CC.GRAY})"
    }

    override fun getAbstractType() = DoubleValue::class.java
}

data class IntValue(
    override var internalValue: Int = 0
) : InternalValue<Int>
{
    override fun randomize(randomizer: Double): Int
    {
        val min = internalValue - randomizer
        val max = internalValue + randomizer
        if (min == max) return internalValue
        val normalizedRange = max(min, 0.0)..max
        return Random.nextInt(
            floor(normalizedRange.start).toInt(),
            ceil(normalizedRange.endInclusive).toInt()
        )
    }

    override fun randomizeRange(randomizer: Double): String
    {
        return "${CC.GRAY}(${CC.WHITE}${
            "%.2f".format(internalValue - randomizer)
        } ${CC.GREEN}◀▶ ${CC.WHITE}${
            "%.2f".format(internalValue + randomizer)
        }${CC.GRAY})"
    }

    override fun getAbstractType() = IntValue::class.java
}

fun Any.toInternalValue() = when (true)
{
    ((this is Boolean) || (this.javaClass == Boolean::class.javaObjectType) || (this.javaClass == Boolean::class.javaPrimitiveType)) -> BooleanValue(this as Boolean)
    ((this is Double) || (this.javaClass == Double::class.javaObjectType) || (this.javaClass == Double::class.javaPrimitiveType)) -> DoubleValue(this as Double)
    ((this is Int) || (this.javaClass == Int::class.javaObjectType) || (this.javaClass == Int::class.javaPrimitiveType)) -> IntValue(this as Int)
    else -> throw IllegalStateException(
        "Unsupported InternalValue type ${javaClass.name} was given"
    )
}
