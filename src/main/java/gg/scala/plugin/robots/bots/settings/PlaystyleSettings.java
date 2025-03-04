package gg.scala.plugin.robots.bots.settings;

import gg.scala.plugin.robots.bot.setting.setting.Category;
import gg.scala.plugin.robots.bot.setting.setting.Option;
import lombok.Getter;
import net.evilblock.cubed.util.CC;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
public class PlaystyleSettings extends Category {

    private final Option<Boolean> hitSelect = new Option<>(
            "Hit Select",
            Material.STICK,
            this,
            false, -1
    ).addDescription(
            CC.GRAY + "Enables hit selection mode",
            CC.GRAY + "to prioritize specific targets."
    );

    private final Option<Integer> hitSelectTicks = new Option<>(
            "Hit Select Ticks",
            Material.STICK,
            this,
            5, 1, 60
    ).addDescription(
            CC.GRAY + "Number of ticks for hit selection",
            CC.GRAY + "mode activation."
    );

    private final Option<Integer> hitSelectDelay = new Option<>(
            "Hit Select Delay",
            Material.STICK,
            this,
            5, 1, 60
    ).addDescription(
            CC.GRAY + "Delay in ticks before switching",
            CC.GRAY + "to the next target in hit select mode."
    );

    private final Option<Integer> jumpComboCPS = new Option<>(
            "Jump Combo CPS",
            Material.STICK,
            this,
            4, 1, 25
    ).addDescription(
            CC.GRAY + "Clicks per second (CPS) when",
            CC.GRAY + "performing jump combos."
    );

    private final Option<Boolean> flips = new Option<>(
            "Flicks",
            Material.SLIME_BALL,
            this,
            false, -1
    ).addDescription(
            CC.GRAY + "Enables random flick movements.",
            CC.GRAY + "Recommended with high rotation speed."
    );

    private final Option<Integer> flipYaw = new Option<>(
            "Flicks Yaw",
            Material.SLIME_BALL,
            this,
            35, 5, 360
    ).addDescription(
            CC.GRAY + "Yaw angle for flick movements.",
            CC.GRAY + "Values like 360, 180, 90..."
    );

    private final Option<Integer> flipPitch = new Option<>(
            "Flicks Pitch",
            Material.SLIME_BALL,
            this,
            5, 2, 25
    ).addDescription(
            CC.GRAY + "Pitch angle for flick movements.",
            CC.GRAY + "Values like 25, 15, 0..."
    );

    private final Option<Integer> flipLong = new Option<>(
            "Flicks Long",
            Material.SLIME_BALL,
            this,
            5, 1, 25
    ).addDescription(
            CC.GRAY + "Duration in ticks for each flick."
    );

    private final Option<Integer> flipChance = new Option<>(
            "Flicks Chance",
            Material.SLIME_BALL,
            this,
            75, 5, 100
    ).addDescription(
            CC.GRAY + "Probability (in %) of performing",
            CC.GRAY + "a flick during combat."
    );

    private final Option<Boolean> flipSync = new Option<>(
            "Flicks Sync with WTaps",
            Material.SLIME_BALL,
            this,
            true, -1
    ).addDescription(
            CC.GRAY + "Synchronizes flick movements",
            CC.GRAY + "with WTaps for coordinated actions."
    );

    private final Option<Boolean> alwaysJump = new Option<>(
            "Always Jump",
            Material.SADDLE,
            this,
            false, -1
    ).addDescription(
            CC.GRAY + "Enables constant jumping during",
            CC.GRAY + "movement."
    );

    private final Option<Boolean> jumpWhileCombo = new Option<>(
            "Jump While Comboed",
            Material.SADDLE,
            this,
            false, -1
    ).addDescription(
            CC.GRAY + "Enables jumping while being",
            CC.GRAY + "comboed by opponents."
    );

    private final Option<Integer> jumpWhileComboHits = new Option<>(
            "Jump While Comboed Hits",
            Material.SADDLE,
            this,
            4, -1
    ).addDescription(
            CC.GRAY + "Number of hits before jumping",
            CC.GRAY + "while being comboed."
    );

    private final Option<Boolean> jumpCombo = new Option<>(
            "Jump Combo",
            Material.SADDLE,
            this,
            false, -1
    ).addDescription(
            CC.GRAY + "Enables jump combos during",
            CC.GRAY + "combat."
    );

    private final Option<Integer> jumpComboHits = new Option<>(
            "Jump Combo Hits",
            Material.SADDLE,
            this,
            4, 1, 30
    ).addDescription(
            CC.GRAY + "Number of hits required to",
            CC.GRAY + "initiate a jump combo."
    );

    public PlaystyleSettings(Category parent) {
        super(
                "playstylesettings",
                Arrays.asList(
                        "Configurable playstyle",
                        "settings for the robot."
                ),
                new ItemStack(Material.ENDER_PEARL),
                parent,
                "Play Style Settings",
                "botgui.playstylesettings"
        );

        addChildren(hitSelect, hitSelectDelay, jumpComboCPS, flips, flipYaw, flipPitch, flipLong, flipChance, flipSync, alwaysJump, jumpWhileCombo, jumpWhileComboHits, jumpCombo, jumpComboHits);
    }
}
