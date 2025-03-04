package gg.scala.plugin.robots.features.impl

/**
 * @author GrowlyX
 * @since 7/24/2024
 */
import org.bukkit.Location
import org.bukkit.util.Vector

fun getFishingRodLaunchVelocity(shooterLocation: Location, targetLocation: Location, launchSpeed: Double): Vector {
    val toTarget = targetLocation.toVector().subtract(shooterLocation.toVector())

    val normalizedDirection = toTarget.normalize()
    val launchVelocity = normalizedDirection.multiply(launchSpeed)

    return launchVelocity
}
