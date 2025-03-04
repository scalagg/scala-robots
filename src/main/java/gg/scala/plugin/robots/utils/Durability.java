package gg.scala.plugin.robots.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.util.Random;

public class Durability {
    public static int getDurabilityLost(ItemStack armorPiece, double damage) {
        double toughness = 0;
        double probability = armorPiece.getEnchantmentLevel(Enchantment.DURABILITY) > 3 ? 1.0 / (3.0 + 1.0) : 1.0 / (armorPiece.getEnchantmentLevel(Enchantment.DURABILITY) + 1.0);

         // Chance of armor to get damage
        if (probability < 1) {
            if (Math.random() < probability) {
                return 0;
            }
        }

        if (armorPiece.getType() == Material.IRON_BOOTS
                || armorPiece.getType() == Material.IRON_LEGGINGS
                || armorPiece.getType() == Material.IRON_CHESTPLATE
                || armorPiece.getType() == Material.IRON_HELMET) {
            toughness = 2.5;
        }

        if (armorPiece.getType() == Material.DIAMOND_BOOTS
                || armorPiece.getType() == Material.DIAMOND_LEGGINGS
                || armorPiece.getType() == Material.DIAMOND_CHESTPLATE
                || armorPiece.getType() == Material.DIAMOND_HELMET) {
            toughness = 3;
        }

        return (int) Math.max(1, (damage * toughness) / 24);
    }
}
