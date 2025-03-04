package gg.scala.plugin.robots.bots.settings;

import gg.scala.plugin.robots.bot.setting.setting.Category;
import gg.scala.plugin.robots.bot.setting.setting.Option;
import lombok.Getter;
import net.evilblock.cubed.util.CC;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
public class BuildingSettings extends Category {

    private final Option<Boolean> enabled = new Option<>(
            "Enabled",
            Material.STONE_BUTTON,
            this,
            true
    ).addDescription(
            CC.GRAY + "Enable or disable the building feature for the bot."
    );

    private final Option<Double> minDistanceFromPlayer = new Option<>(
            "Min Distance From Player",
            Material.PRISMARINE_SHARD,
            this,
            2.5,
            0.5,
            10
    ).addDescription(
            CC.GRAY + "The minimum distance from the player",
            CC.GRAY + "at which the bot will start building."
    );

    private final Option<Integer> placeCooldown = new Option<>(
            "Place Cooldown",
            Material.CAULDRON_ITEM,
            this,
            5000,
            250,
            100000
    ).addDescription(
            CC.GRAY + "The cooldown time in milliseconds",
            CC.GRAY + "(1000 ms = 1 second) before the bot",
            CC.GRAY + "can place another block."
    );

    private final Option<Integer> buildingTicks = new Option<>(
            "Building Ticks",
            Material.GHAST_TEAR,
            this,
            8,
            2,
            120
    ).addDescription(
            CC.GRAY + "The number of ticks (20 ticks = 1 second)",
            CC.GRAY + "the bot takes to build."
    );

    private final Option<Boolean> buildWhenCombo = new Option<>(
            "Build When Comboed",
            Material.SADDLE,
            this,
            false
    ).addDescription(
            CC.GRAY + "Whether the bot will start building",
            CC.GRAY + "when it is being comboed by a player."
    );

    private final Option<Integer> buildWhenComboHits = new Option<>(
            "Build When Comboed Hits",
            Material.IRON_BARDING,
            this,
            3,
            1,
            100
    ).addDescription(
            CC.GRAY + "The number of hits taken while being comboed",
            CC.GRAY + "before the bot starts building."
    );

    private final Option<Boolean> buildWhenPlayerIsHigh = new Option<>(
            "Build When Player Is High",
            Material.CLAY_BALL,
            this,
            true
    ).addDescription(
            CC.GRAY + "Whether the bot will build up when",
            CC.GRAY + "the player is at a higher elevation."
    );

    private final Option<Integer> buildWhenPlayerIsHighBlocks = new Option<>(
            "Build When Player Is High Blocks",
            Material.CLAY_BRICK,
            this,
            3,
            1,
            20
    ).addDescription(
            CC.GRAY + "The number of blocks the player needs to be",
            CC.GRAY + "above the bot before it starts building up."
    );

    private final Option<Integer> buildingUpTicks = new Option<>(
            "Building Up Ticks",
            Material.GHAST_TEAR,
            this,
            20,
            5,
            240
    ).addDescription(
            CC.GRAY + "The number of ticks (20 ticks = 1 second)",
            CC.GRAY + "the bot takes to build up."
    );

    public BuildingSettings(Category parent) {
        super(
                "buildingsettings",
                Arrays.asList(
                        "Configurable building",
                        "settings for the robot."
                ),
                new ItemStack(Material.WOOL, 1, (short) 11),
                parent,
                "Building Settings",
                "botgui.buildingsettings"
        );

        addChildren(enabled, minDistanceFromPlayer, placeCooldown, buildingTicks, buildWhenCombo, buildWhenComboHits, buildWhenPlayerIsHigh, buildWhenPlayerIsHighBlocks, buildingUpTicks);
    }
}
