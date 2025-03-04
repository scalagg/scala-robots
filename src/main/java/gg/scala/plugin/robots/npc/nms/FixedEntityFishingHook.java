package gg.scala.plugin.robots.npc.nms;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;

import java.util.List;

public class FixedEntityFishingHook extends EntityFishingHook {
    public FixedEntityFishingHook(World world) {
        super(world);
    }

    public FixedEntityFishingHook(World world, EntityHuman entityhuman) {
        super(world, entityhuman);
    }

    @Override
    public void die() {
        super.die();
    }
}
