package gg.scala.plugin.robots.bots.settings;

import gg.scala.plugin.robots.bot.setting.setting.Category;
import gg.scala.plugin.robots.bot.setting.setting.Option;
import lombok.Getter;
import net.evilblock.cubed.util.CC;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
public class RodSettings extends Category {

    private final Option<Boolean> enabled = new Option<>(
            "Rod Enabled",
            Material.FISHING_ROD,
            this,
            false,
            -1
    ).addDescription(
            CC.GRAY + "Toggle the use of the fishing rod",
            CC.GRAY + "in combat."
    );

    private final Option<Double> minRodDistance = new Option<>(
            "Minimum Rod Throwing Distance",
            Material.FISHING_ROD,
            this,
            5.0,
            0.1,
            25
    ).addDescription(
            CC.GRAY + "The minimum distance at which",
            CC.GRAY + "the bot will use the fishing rod."
    );

    private final Option<Double> maxRodDistance = new Option<>(
            "Maximum Rod Throwing Distance",
            Material.FISHING_ROD,
            this,
            7.5,
            0.1,
            100
    ).addDescription(
            CC.GRAY + "The maximum distance at which",
            CC.GRAY + "the bot will use the fishing rod."
    );

    private final Option<Integer> rodDuration = new Option<>(
            "Rod Duration (MS)",
            Material.FISHING_ROD,
            this,
            750,
            10,
            2500
    ).addDescription(
            CC.GRAY + "The duration, in milliseconds,",
            CC.GRAY + "that the bot will wait after",
            CC.GRAY + "throwing a rod. (1000ms = 1s)"
    );

    private final Option<Boolean> rodWhileInCombo = new Option<>(
            "Throw Rod While In Combo",
            Material.FISHING_ROD,
            this,
            false,
            -1
    ).addDescription(
            CC.GRAY + "Toggle whether the bot will use",
            CC.GRAY + "the fishing rod while in a combo."
    );

    public RodSettings(Category parent) {
        super(
                "rodsettings",
                Arrays.asList(
                        "Configurable rod",
                        "settings for the robot."
                ),
                new ItemStack(Material.FISHING_ROD),
                parent,
                "Rod Settings",
                "botgui.rodsettings"
        );

        addChildren(enabled, minRodDistance, maxRodDistance, rodDuration, rodWhileInCombo);
    }
}
