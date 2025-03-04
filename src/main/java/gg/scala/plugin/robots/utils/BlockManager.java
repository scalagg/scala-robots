package gg.scala.plugin.robots.utils;

import gg.scala.plugin.robots.utils.objects.AABB;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.IBlockData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.util.Vector;

public class BlockManager {

    public static Material getBlockType(Location location) {
        Block block = location.getBlock();
        Block belowBlock = location.getBlock();

        Material material = block.getType();
        AABB hitBox = blockHitBox(block);
        AABB belowBlockHitBox = blockHitBox(belowBlock);

        if (!hitBox.isColliding(location.toVector())) return Material.AIR;
        if (belowBlockHitBox.isColliding(location.toVector())) return belowBlock.getType();

        return material;
    }

    public static AABB blockHitBox(Block block) {
        IBlockData blockData = ((CraftWorld) block.getWorld()).getHandle().getType(new BlockPosition(block.getX(), block.getY(), block.getZ()));
        net.minecraft.server.v1_8_R3.Block blockNative = blockData.getBlock();
        blockNative.updateShape(((CraftWorld) block.getWorld()).getHandle(), new BlockPosition(block.getX(), block.getY(), block.getZ()));
        Vector min = new Vector((double) block.getX() + blockNative.B(), (double) block.getY() + blockNative.D(), (double) block.getZ() + blockNative.F());
        Vector max = new Vector((double) block.getX() + blockNative.C(), (double) block.getY() + blockNative.E(), (double) block.getZ() + blockNative.G());

        return new AABB(min,max);
    }
}
