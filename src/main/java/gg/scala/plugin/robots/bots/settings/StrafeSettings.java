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
public class StrafeSettings extends Category {

    public Option<Boolean> enabled = new Option<>(
            "Strafe",
            Material.SUGAR_CANE,
            this,
            false,
            -1
    ).addDescription(
            CC.GRAY + "Enable or disable the bot's strafing behavior.",
            CC.GRAY + "Strafing involves moving side to side during",
            CC.GRAY + "combat to avoid attacks and make the bot harder to hit."
    );

    public Option<Integer> chance = new Option<>(
            "Chance",
            Material.GOLDEN_CARROT,
            this,
            100,
            1,
            100
    ).addDescription(
            CC.GRAY + "The probability, as a percentage, that the bot will",
            CC.GRAY + "perform a strafe move during combat. Set to 100% to",
            CC.GRAY + "always strafe, or lower for more random behavior."
    );

    public Option<Boolean> strafeInCombo = new Option<>(
            "Strafe In Combo",
            Material.SUGAR,
            this,
            false,
            -1
    ).addDescription(
            CC.GRAY + "Enable or disable strafing specifically when the bot",
            CC.GRAY + "is in a combo. This can help the bot avoid being hit",
            CC.GRAY + "while attacking."
    );

    public Option<Integer> switchSideTicks = new Option<>(
            "Switch Side Ticks",
            Material.TRAP_DOOR,
            this,
            20,
            1,
            120
    ).addDescription(
            CC.GRAY + "The number of ticks between each switch in strafing",
            CC.GRAY + "direction. This controls how often the bot changes",
            CC.GRAY + "from moving left to right or vice versa during strafing."
    );

    public StrafeSettings(Category parent) {
        super(
                "strafesettings",
                Arrays.asList(
                        "Configurable strafe",
                        "settings for the robot."
                ),
                new ItemStack(Material.SUGAR_CANE),
                parent,
                "Strafe Settings",
                "botgui.strafesettings"
        );

        addChildren(enabled, chance, strafeInCombo, switchSideTicks);
    }
}

