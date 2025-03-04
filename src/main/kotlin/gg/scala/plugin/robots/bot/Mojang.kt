package gg.scala.plugin.robots.bot

import com.mojang.authlib.properties.Property
import gg.scala.cache.uuid.ScalaStoreUuidCache
import gg.scala.lemon.util.QuickAccess.uniqueId
import gg.scala.practice.namespace
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.util.bukkit.Tasks
import java.net.URL
import java.util.UUID

/**
 * @author GrowlyX
 * @since 7/21/2024
 */
data class MojangProfile(
    val id: String,
    val name: String,
    val properties: List<Property>
)
{
    fun extractTextures() = properties.firstOrNull { it.name == "textures" }
}

fun getProfile(username: String): MojangProfile?
{
    val uniqueId = ScalaStoreUuidCache
        .uniqueId(username)
        ?: return null

    val existing = ScalaCommonsSpigot.instance.kvConnection.sync()
        .get("${namespace()}:player-profiles:$uniqueId")

    if (existing != null)
    {
        return Serializers.gson.fromJson(
            existing, MojangProfile::class.java
        )
    }

    val sessionServerResponse = runCatching {
        URL("https://sessionserver.mojang.com/session/minecraft/profile/$uniqueId?unsigned=false")
            .readText()
    }.getOrNull()
        ?: return null

    val mojangProfile = runCatching { Serializers.gson
        .fromJson(sessionServerResponse, MojangProfile::class.java) }
        .getOrNull()
        ?: return null

    ScalaCommonsSpigot.instance.kvConnection.sync().setex(
        "${namespace()}:player-profiles:$uniqueId",
        3600 * 6L,
        sessionServerResponse
    )

    return mojangProfile
}
