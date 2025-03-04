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
public class EatingSettings extends Category {

    private Option<Integer> startEatingTimer = new Option<>(
            "Start Eating Timer",
            Material.GOLDEN_APPLE,
            this,
            40,
            2,
            300
    ).addDescription(
            CC.GRAY + "The time in ticks (20 ticks = 1 second)",
            CC.GRAY + "before the bot starts eating."
    );

    private Option<Double> startEatingDistance = new Option<>(
            "Start Eating Distance",
            Material.MUSHROOM_SOUP,
            this,
            3.0,
            1,
            20
    ).addDescription(
            CC.GRAY + "The distance from an opponent at",
            CC.GRAY + "which the bot starts eating."
    );

    private Option<Integer> eatingGappleHealth = new Option<>(
            "Eating Gapple Health",
            Material.GOLDEN_APPLE,
            this,
            15,
            1,
            30
    ).addDescription(
            CC.GRAY + "The bot's health at which it decides",
            CC.GRAY + "to eat a golden apple."
    );

    private Option<Double> cancelEatingDistance = new Option<>(
            "Jump Eating Distance",
            Material.BARRIER,
            this,
            4.5,
            0.1,
            15
    ).addDescription(
            CC.GRAY + "The distance at which the bot will",
            CC.GRAY + "stop eating and jump away."
    );

    private Option<Boolean> preGap = new Option<>(
            "Pre Gappling",
            Material.GOLDEN_CARROT,
            this,
            true,
            -1
    ).addDescription(
            CC.GRAY + "Whether the bot will eat a golden",
            CC.GRAY + "apple before engaging in combat."
    );

    public EatingSettings(Category parent) {
        super(
                "gapplesettings",
                Arrays.asList(
                        "Configurable gapple",
                        "settings for the robot."
                ),
                new ItemStack(Material.GOLDEN_APPLE),
                parent,
                "Gapple Settings",
                "botgui.gapplesettings"
        );
        addChildren(preGap, startEatingDistance, startEatingTimer, eatingGappleHealth, cancelEatingDistance);
    }
}
