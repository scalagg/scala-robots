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
public class FleeSettings extends Category {

    private Option<Double> calculatedDistance = new Option<>(
            "Calculated Distance",
            Material.WATCH,
            this,
            10.0,
            1,
            40
    ).addDescription(
            CC.GRAY + "The distance the bot will",
            CC.GRAY + "calculate for positioning."
    );

    private Option<Boolean> runFromCombos = new Option<>(
            "Run From Combos",
            Material.DIAMOND_BOOTS,
            this,
            false,
            -1
    ).addDescription(
            CC.GRAY + "Toggle whether the bot will",
            CC.GRAY + "attempt to run away from",
            CC.GRAY + "combos."
    );

    private Option<Integer> runFromCombosHits = new Option<>(
            "Run From Combo Hits",
            Material.DIAMOND_SWORD,
            this,
            3,
            1,
            20
    ).addDescription(
            CC.GRAY + "The number of hits the bot",
            CC.GRAY + "will take before attempting",
            CC.GRAY + "to run from a combo."
    );

    private Option<Integer> runFromCombosTime = new Option<>(
            "Run From Combo Ticks",
            Material.WATCH,
            this,
            20,
            1,
            250
    ).addDescription(
            CC.GRAY + "The duration, in ticks, that the",
            CC.GRAY + "bot will attempt to run from",
            CC.GRAY + "a combo. (20 ticks = 1 second)"
    );

    private Option<Integer> runningRotationSpeed = new Option<>(
            "Run Rotation Speed",
            Material.WEB,
            this,
            40,
            1,
            200
    ).addDescription(
            CC.GRAY + "The speed at which the bot",
            CC.GRAY + "will rotate while running."
    );

    public FleeSettings(Category parent) {
        super(
                "fleesettings",
                Arrays.asList(
                        "Configurable flee",
                        "settings for the robot."
                ),
                new ItemStack(Material.FEATHER),
                parent,
                "Flee Settings",
                "botgui.fleesettings"
        );
        addChildren(calculatedDistance, runFromCombos, runFromCombosHits, runFromCombosTime, runningRotationSpeed);
    }
}