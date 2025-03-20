package gg.scala.plugin.robots.integration.practice.freebotweekends

import net.evilblock.cubed.util.CC
import java.time.*

/**
 * @author GrowlyX
 * @since 8/9/2024
 */
object FreeBotWeekends
{
    private val newYorkLocalEST = ZoneId.of("America/New_York")

    private val fbfStart = LocalTime.of(15, 30) // 3:30PM EST
    private val fbfEnd = LocalTime.of(22, 0) // 10PM EST

    fun isFreeBotWeekends(): Boolean
    {
        val now = ZonedDateTime.now(newYorkLocalEST)
        val currentTime = now.toLocalTime()

        return (now.dayOfWeek == DayOfWeek.FRIDAY &&
            currentTime.isAfter(fbfStart)) || (now.dayOfWeek == DayOfWeek.SATURDAY && currentTime.isBefore(fbfEnd))
    }

    fun timeUntilNextFreeBotFriday(): Duration
    {
        val now = ZonedDateTime.now(newYorkLocalEST)

        val nextFriday = now.withDayOfWeek(DayOfWeek.FRIDAY).with(fbfStart)
        val targetDateTime = if (now.dayOfWeek == DayOfWeek.FRIDAY && now.toLocalTime().isAfter(fbfStart))
        {
            nextFriday.plusWeeks(1)
        } else
        {
            nextFriday
        }

        // Calculate the duration between now and the target date and time
        val duration = Duration.between(now, targetDateTime)
        return duration
    }

    fun ZonedDateTime.withDayOfWeek(dayOfWeek: DayOfWeek): ZonedDateTime
    {
        var result = this.with(dayOfWeek)
        if (result.isBefore(this))
        {
            result = result.plusWeeks(1)
        }
        return result
    }

    @JvmStatic
    fun formatIntoAbbreviatedString(secs: Int): String {
        if (secs == 0) {
            return "0s"
        }

        val remainder = secs % 86400
        val days = secs / 86400
        val hours = remainder / 3600
        val minutes = remainder / 60 - hours * 60
        val seconds = remainder % 3600 - minutes * 60

        val fDays = if (days > 0) {
            " " + days + "d"
        } else {
            ""
        }

        val fHours = if (hours > 0) {
            " " + hours + "h"
        } else {
            ""
        }

        val fMinutes = if (minutes > 0) {
            " " + minutes + "m"
        } else {
            ""
        }

        val fSeconds = if (seconds > 0) {
            " " + seconds + "s"
        } else {
            ""
        }

        return (fDays + fHours + fMinutes + fSeconds).trim { it <= ' ' }
    }

    fun fancyStatus(): String
    {
        if (isFreeBotWeekends())
        {
            return "${CC.GREEN}NOW!"
        }

        return "${CC.GREEN}${
            formatIntoAbbreviatedString(
            timeUntilNextFreeBotFriday()
                .toSeconds()
                .toInt()
        )
        }"
    }
}
