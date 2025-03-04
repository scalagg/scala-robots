package gg.scala.plugin.robots.bots.settings;

import gg.scala.plugin.robots.bot.setting.setting.Category;
import gg.scala.plugin.robots.bot.setting.setting.Option;
import lombok.Getter;
import lombok.Setter;
import net.evilblock.cubed.util.CC;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
@Setter
public class VelocitySettings extends Category {

    private Option<Double> horizontal = new Option<>(
            "Horizontal",
            Material.ANVIL,
            this,
            0.47,
            0.01,
            5
    ).addDescription(
            CC.GRAY + "The horizontal movement factor of the bot.",
            CC.GRAY + "A higher value increases the bot's ability",
            CC.GRAY + "to move horizontally. Adjust based on desired",
            CC.GRAY + "movement responsiveness."
    );

    private Option<Double> vertical = new Option<>(
            "Vertical",
            Material.ANVIL,
            this,
            0.35,
            0.01,
            5
    ).addDescription(
            CC.GRAY + "The vertical movement factor of the bot.",
            CC.GRAY + "This affects the bot's ability to move up",
            CC.GRAY + "and down. Higher values increase vertical",
            CC.GRAY + "mobility."
    );

    private Option<Double> dependentRange = new Option<>(
            "Dependent Range",
            Material.BLAZE_ROD,
            this,
            3.2,
            0.1,
            10
    ).addDescription(
            CC.GRAY + "The range at which the bot's movement speed",
            CC.GRAY + "becomes dependent on its distance to the target.",
            CC.GRAY + "Higher values mean speed adjustments occur",
            CC.GRAY + "at greater distances."
    );

    private Option<Double> dependentRangeMultiplier = new Option<>(
            "Dependent Range Multiplier",
            Material.BLAZE_POWDER,
            this,
            0.15,
            0.01,
            5
    ).addDescription(
            CC.GRAY + "The multiplier for speed adjustments based on",
            CC.GRAY + "the dependent range. Higher values increase the",
            CC.GRAY + "effect of distance on the bot's movement speed."
    );

    private Option<Double> motionFriction = new Option<>(
            "Motion Friction",
            Material.GOLD_BLOCK,
            this,
            1.0,
            0.02,
            5
    ).addDescription(
            CC.GRAY + "The amount of friction applied to the bot's",
            CC.GRAY + "motion. Higher values decrease the bot's overall",
            CC.GRAY + "speed by increasing resistance."
    );

    private Option<Double> sprintMultiplier = new Option<>(
            "Sprint Multiplier",
            Material.DIAMOND_HOE,
            this,
            1.2,
            0.1,
            10
    ).addDescription(
            CC.GRAY + "The speed multiplier when the bot is sprinting.",
            CC.GRAY + "A higher value increases the bot's sprint speed."
    );

    private Option<Double> groundMultiplier = new Option<>(
            "Ground Multiplier",
            Material.DIAMOND_SPADE,
            this,
            1.1,
            0.1,
            10
    ).addDescription(
            CC.GRAY + "The movement speed multiplier when the bot is on",
            CC.GRAY + "the ground. A higher value increases the bot's",
            CC.GRAY + "speed while grounded."
    );

    public VelocitySettings(Category parent) {
        super(
                "velocitysettings",
                Arrays.asList(
                        "Configurable velocity",
                        "settings for the robot."
                ),
                new ItemStack(Material.SLIME_BALL),
                parent,
                "Velocity Settings",
                "botgui.velocitysettings"
        );

        addChildren(horizontal, vertical, dependentRange, dependentRangeMultiplier, motionFriction, sprintMultiplier, groundMultiplier);
    }
}
