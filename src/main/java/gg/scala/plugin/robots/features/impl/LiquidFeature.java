package gg.scala.plugin.robots.features.impl;

import gg.scala.plugin.robots.bots.Bot;
import gg.scala.plugin.robots.features.Feature;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class LiquidFeature extends Feature {
    @Override
    public void tickEvent(Bot bot) {
        Block block = bot.getMovements().getLocation().getBlock();
        Block above = bot.getMovements().getLocation().clone().add(0, 1, 0).getBlock();
        if (!block.isLiquid()) return;

        boolean lava = block.getType().equals(Material.LAVA) || block.getType().equals(Material.STATIONARY_LAVA) || above.getType().equals(Material.LAVA) || above.getType().equals(Material.STATIONARY_LAVA);
        boolean water = block.getType().equals(Material.WATER) || block.getType().equals(Material.STATIONARY_WATER) || above.getType().equals(Material.WATER) || above.getType().equals(Material.STATIONARY_WATER);

        if (block.getType().equals(Material.FIRE)) {
            int fireRess = bot.getEffectsManager().getLevel(PotionEffectType.FIRE_RESISTANCE);

            if (fireRess != 0) return;
            if (System.currentTimeMillis() - 500 < bot.getLastLiquid()) return;

            bot.setLastLiquid(System.currentTimeMillis());

            bot.getDamageManager().playSound();
            bot.getDamageManager().damageEffect();
            bot.getHealthManager().removeHealth(1);
            bot.getHealthManager().fire(120);
        }

        if (lava) {
            int fireRess = bot.getEffectsManager().getLevel(PotionEffectType.FIRE_RESISTANCE);

            if (fireRess != 0) return;
            if (System.currentTimeMillis() - 1000 < bot.getLastLiquid()) return;

            bot.setLastLiquid(System.currentTimeMillis());

            bot.getDamageManager().playSound();
            bot.getDamageManager().damageEffect();
            bot.getHealthManager().removeHealth(2);
            bot.getHealthManager().fire(300);
        }

        if (water) {
            bot.getPacketsManager().setPlayerOnFirePackets(false);
            bot.getHealthManager().setFireTicks(0);
        }

        if (water || lava) {
            // Apply movement effect
            applyWaterFlow(bot);
        }
    }

    private void applyWaterFlow(Bot bot) {
        Block botBlock = bot.getMovements().getLocation().getBlock();

        Vector flowDirection = new Vector(0, 0, 0);

        // Iterate over adjacent blocks
        for (BlockFace face : BlockFace.values()) {
            Block adjacentBlock = botBlock.getRelative(face);

            if ((adjacentBlock.getType() == Material.WATER || adjacentBlock.getType() == Material.STATIONARY_WATER)  && getWaterLevel(adjacentBlock) < getWaterLevel(botBlock)) {
                // Add direction vector based on BlockFace direction
                flowDirection.add(getVectorFromBlockFace(face).multiply(getWaterLevel(botBlock) - getWaterLevel(adjacentBlock)));
            }
        }

        // Normalize flow direction to ensure it's a unit vector
        if (flowDirection.length() > 0) {
            flowDirection.normalize();
        }

        // Apply water friction
        bot.getVelocityCalculator().setAccVelocity(flowDirection.multiply(-0.05));
    }

    private Vector getVectorFromBlockFace(BlockFace face) {
        switch (face) {
            case NORTH: return new Vector(0, 0, -1);
            case EAST:  return new Vector(1, 0, 0);
            case SOUTH: return new Vector(0, 0, 1);
            case WEST:  return new Vector(-1, 0, 0);
            case UP:    return new Vector(0, 1, 0);
            case DOWN:  return new Vector(0, -1, 0);
            default:    return new Vector(0, 0, 0); // Handle cases like SELF or invalid faces
        }
    }

    private int getWaterLevel(Block block) {
        // This method assumes that a Block is water. You can skip this check if you're sure.
        if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER) {
            byte data = block.getData(); // Get block data (metadata) directly
            return data & 0x7; // Water levels are in the lower 3 bits (0-7)
        }
        return 8; // Return 8 if the block is not water, assuming no level
    }
}
