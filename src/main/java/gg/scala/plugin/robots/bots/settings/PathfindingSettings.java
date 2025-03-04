package gg.scala.plugin.robots.bots.settings;

import gg.scala.plugin.robots.bot.setting.setting.Category;
import gg.scala.plugin.robots.bot.setting.setting.Option;
import lombok.Getter;
import net.evilblock.cubed.util.CC;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
public class PathfindingSettings extends Category {

    private final Option<Boolean> enabled = new Option<>(
            "Enabled",
            Material.STONE_BUTTON,
            this,
            true
    ).addDescription(
            CC.GRAY + "Toggle to enable or disable",
            CC.GRAY + "the specific feature."
    );

    private final Option<Double> distanceFromLastLocation = new Option<>(
            "Distance From Last Location",
            Material.STRING,
            this,
            0.1, 0.05, 2
    ).addDescription(
            CC.GRAY + "The minimum distance the bot",
            CC.GRAY + "must move from its last location."
    );

    private final Option<Integer> getPathCooldown = new Option<>(
            "Get Path Cooldown",
            Material.BLAZE_POWDER,
            this,
            500, 100, 5000
    ).addDescription(
            CC.GRAY + "Cooldown time in milliseconds",
            CC.GRAY + "before recalculating the path.",
            CC.GRAY + "1000 ms equals 1 second."
    );

    private final Option<Double> minDistanceFromPlayer = new Option<>(
            "Min Distance From Player",
            Material.BOWL,
            this,
            1.0, 0.1, 10
    ).addDescription(
            CC.GRAY + "The minimum distance the bot",
            CC.GRAY + "should maintain from players."
    );

    private final Option<Integer> cooldownAfterAttack = new Option<>(
            "Cooldown After Attack",
            Material.GOLD_NUGGET,
            this,
            4000, 250, 15000
    ).addDescription(
            CC.GRAY + "Cooldown time in milliseconds",
            CC.GRAY + "after an attack before the bot",
            CC.GRAY + "can perform another action.",
            CC.GRAY + "1000 ms equals 1 second."
    );

    public PathfindingSettings(Category parent) {
        super(
                "pathfindingsettings",
                Arrays.asList(
                        "Configurable pathfinding",
                        "settings for the robot."
                ),
                new ItemStack(Material.DETECTOR_RAIL),
                parent,
                "Pathfinding Settings",
                "botgui.pathfindingsettings"
        );

        addChildren(enabled, distanceFromLastLocation, getPathCooldown, minDistanceFromPlayer, cooldownAfterAttack);
    }
}