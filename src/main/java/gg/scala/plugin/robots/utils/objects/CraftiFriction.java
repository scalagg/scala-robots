package gg.scala.plugin.robots.utils.objects;

import org.bukkit.Material;
import org.bukkit.block.Block;

public enum CraftiFriction {

    DEFAULT(0.6f),
    ICE(0.98f),
    PACKED_ICE(0.98F),
    SLIME_BLOCK(0.8f);

    private final float factor;

    CraftiFriction(final float factor) {
        this.factor = factor;
    }
    public float getFactor() {
        return this.factor;
    }

    public static float getFactor(final Block block) {
        final Material type = block.getType();
        try {
            return valueOf(type.name()).getFactor();
        } catch (final Exception exception) {
            return DEFAULT.getFactor();
        }
    }

    public static float getFactor(final Material type) {
        if (type.equals(Material.ICE) || type.equals(Material.PACKED_ICE)) return ICE.factor;
        if (type.equals(Material.SLIME_BLOCK)) return SLIME_BLOCK.factor;
        return DEFAULT.factor;
    }
}
