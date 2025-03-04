package gg.scala.plugin.robots.features.impl

import gg.scala.plugin.robots.utils.Nms
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.math.acos

/**
 * @author GrowlyX
 * @since 7/24/2024
 */
fun isInLineOfSight(playerDirection: Vector, playerLocation: Location, targetLocation: Location, thresholdDegrees: Double): Boolean {
    val toTarget = targetLocation.toVector().subtract(playerLocation.toVector())

    val dotProduct = playerDirection.dot(toTarget)
    val magnitudePlayerDirection = playerDirection.length()
    val magnitudeToTarget = toTarget.length()

    val cosAngle = dotProduct / (magnitudePlayerDirection * magnitudeToTarget)
    val clampedCosAngle = cosAngle.coerceIn(-1.0, 1.0)

    val angleRadians = acos(clampedCosAngle)
    val angleDegrees = Math.toDegrees(angleRadians)

    return angleDegrees <= thresholdDegrees
}

@JvmOverloads
fun isMovingTowards(
    player1: Location,
    player2: Location,
    threshold: Double = 0.1
): Boolean {
    val direction1 = player1.direction
    val direction2 = player2.direction

    val toPlayer2 = player2.toVector().subtract(player1.toVector()).normalize()

    val isMovingTowards = direction1.dot(toPlayer2) > threshold
    val isMovingBackwards = direction2.dot(toPlayer2) < -threshold

    return isMovingTowards && isMovingBackwards
}


fun isLocationInsidePlayerHitbox(location: Location, player: Player): Boolean {
    val playerLocation = Nms.entityHitBox(player)
    return playerLocation.isColliding(location.toVector())
}
