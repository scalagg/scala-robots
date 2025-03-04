package gg.scala.plugin.robots.bot.setting.setting

import gg.scala.plugin.robots.bots.settings.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 7/21/2024
 */
val root = Category(
    "root",
    listOf(),
    ItemStack(Material.AIR),
    null,
    "PvP Bot Settings",
    null
).apply {
    addChildren(
        AttackSettings(this),
        ConnectionSettings(this),
        EatingSettings(this),
        FleeSettings(this),
        PacketsSettings(this),
        PearlSettings(this),
        PlaystyleSettings(this),
        PotionSettings(this),
        RodSettings(this),
        RotationSettings(this),
        StrafeSettings(this),
        VelocitySettings(this),
        WTapSettings(this),
        PathfindingSettings(this),
        BuildingSettings(this)
    )
}

object RootAccessor
{
    val attackSettings = root.childByType<AttackSettings>("root.attacksettings")
    val eatingSettings = root.childByType<EatingSettings>("root.gapplesettings")
    val connectionSettings = root.childByType<ConnectionSettings>("root.connectionsettings")
    val fleeSettings = root.childByType<FleeSettings>("root.fleesettings")
    val packetSettings = root.childByType <PacketsSettings>("root.packetsettings")
    val pearlSettings = root.childByType <PearlSettings>("root.pearlsettings")
    val playStyleSettings = root.childByType <PlaystyleSettings>("root.playstylesettings")
    val potionSettings = root.childByType <PotionSettings>("root.potionsettings")
    val rodSettings = root.childByType <RodSettings>("root.rodsettings")
    val rotationSettings = root.childByType <RotationSettings>("root.rotationsettings")
    val strafeSettings = root.childByType <StrafeSettings>("root.strafesettings")
    val velocitySettings = root.childByType <VelocitySettings>("root.velocitysettings")
    val wTapSettings = root.childByType <WTapSettings>("root.wtapsettings")
    val pathfindingSetting = root.childByType <PathfindingSettings>("root.pathfindingsettings")
    val buildingSetting = root.childByType <BuildingSettings>("root.buildingsettings")
}
