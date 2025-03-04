package gg.scala.plugin.robots.bots.settings;

import gg.scala.plugin.robots.bot.setting.setting.Category;
import gg.scala.plugin.robots.bot.setting.setting.Option;
import lombok.Getter;
import net.evilblock.cubed.util.CC;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
public class PearlSettings extends Category {

    //TODO add pearl chance.
    private Option<Integer> pearlDelay = new Option<>(
            "Pearl Delay",
            Material.ENDER_PEARL,
            this,
            600, 5, 2000
    ).addDescription(
            CC.GRAY + "The delay in ticks between",
            CC.GRAY + "each ender pearl throw."
    );

    private Option<Integer> angleDifference = new Option<>(
            "Angle Difference",
            Material.NETHER_STAR,
            this,
            130, 5, 400
    ).addDescription(
            CC.GRAY + "The maximum angle difference",
            CC.GRAY + "allowed for pearl throws."
    );

    private Option<Integer> pearlThrowYawOffSet = new Option<>(
            "Pearl Throw YawOffSet",
            Material.NAME_TAG,
            this,
            10, 1, 40
    ).addDescription(
            CC.GRAY + "Yaw offset for adjusting",
            CC.GRAY + "the direction of pearl throws."
    );

    private Option<Integer> pearlingTicks = new Option<>(
            "Pearling Ticks",
            Material.WATCH,
            this,
            10, 1, 60
    ).addDescription(
            CC.GRAY + "The number of ticks it takes",
            CC.GRAY + "to perform a pearl throw action."
    );

    public PearlSettings(Category parent) {
        super(
                "pearlsettings",
                Arrays.asList(
                        "Configurable pearl",
                        "settings for the robot."
                ),
                new ItemStack(Material.ENDER_PEARL),
                parent,
                "Pearl Settings",
                "botgui.pearlsettings"
        );
        addChildren(pearlDelay, angleDifference, pearlThrowYawOffSet, pearlingTicks);
    }
}
