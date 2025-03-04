package gg.scala.plugin.robots.utils;

import gg.scala.plugin.robots.utils.objects.AABB;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class Nms {

    public static EntityPlayer getPlayer(Player player) {
        return  ((CraftPlayer) player).getHandle();
    }

    public static void sendPacket(@NotNull Player player, @NotNull Packet<?> packet) {
        getPlayer(player).playerConnection.sendPacket(packet);
    }

    public static Entity getCraftEntity(org.bukkit.entity.Entity entity) {
        return ((CraftEntity) entity).getHandle();
    }

    public static World getCraftWorld(org.bukkit.World world) {
        return ((CraftWorld) world).getHandle();
    }


    @NotNull
    public static AABB entityHitBox(org.bukkit.entity.Entity entity) {
        AxisAlignedBB bb = ((CraftEntity) entity).getHandle().getBoundingBox();
        Vector min = new Vector(bb.a,bb.b,bb.c);
        Vector max = new Vector(bb.d,bb.e,bb.f);

        return new AABB(min,max);
    }
}
