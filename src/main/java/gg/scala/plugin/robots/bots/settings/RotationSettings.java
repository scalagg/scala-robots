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
public class RotationSettings extends Category {

    private Option<Integer> speed = new Option<>(
            "Speed",
            Material.EXP_BOTTLE,
            this,
            18,
            2,
            60
    ).addDescription(
            CC.GRAY + "The movement speed of the bot, measured",
            CC.GRAY + "in units per second. This determines how",
            CC.GRAY + "fast the bot moves around the environment."
    );

    private Option<Integer> jitterRotation = new Option<>(
            "Jitter Rotation",
            Material.FLINT,
            this,
            1,
            1,
            35
    ).addDescription(
            CC.GRAY + "The amount of jitter in the bot's rotation.",
            CC.GRAY + "This simulates erratic movement patterns,",
            CC.GRAY + "making the bot's movement appear less predictable."
    );

    private Option<Integer> speedRandom = new Option<>(
            "Speed Random",
            Material.FIREWORK,
            this,
            1,
            1,
            35
    ).addDescription(
            CC.GRAY + "The degree of randomness applied to the bot's",
            CC.GRAY + "movement speed. This can be used to create",
            CC.GRAY + "unpredictable movement patterns for the bot."
    );

    public RotationSettings(Category parent) {
        super(
                "rotationsettings",
                Arrays.asList(
                        "Configurable rotation",
                        "settings for the robot."
                ),
                new ItemStack(Material.WEB),
                parent,
                "Rotation Settings",
                "botgui.rotationsettings"
        );

        addChildren(speed, jitterRotation, speedRandom);
    }
}
