package gg.scala.plugin.robots.npc;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Set;

public class NPCPlayerConnection extends PlayerConnection {

    public NPCPlayerConnection(MinecraftServer minecraftserver, NetworkManager networkmanager, EntityPlayer entityplayer) {
        super(minecraftserver, networkmanager, entityplayer);
    }
}
