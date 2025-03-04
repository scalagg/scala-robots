package gg.scala.plugin.robots.bots.settings;

import gg.scala.plugin.robots.bot.setting.setting.Category;
import gg.scala.plugin.robots.bot.setting.setting.Option;
import lombok.Getter;
import net.evilblock.cubed.util.CC;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
public class WTapSettings extends Category {

    private final Option<Boolean> enabled = new Option<>(
            "W-Tap",
            Material.DIAMOND_HELMET,
            this,
            true,
            -1
    ).addDescription(
            CC.GRAY + "Enables or disables the W-Tap feature.",
            CC.GRAY + "W-Tap involves resetting sprinting to increase",
            CC.GRAY + "knockback dealt to opponents."
    );

    private final Option<Integer> chance = new Option<>(
            "Chance",
            Material.DIAMOND_CHESTPLATE,
            this,
            100,
            25,
            100
    ).addDescription(
            CC.GRAY + "The likelihood (in percentage) of the bot",
            CC.GRAY + "performing a W-Tap. A higher value means",
            CC.GRAY + "the bot is more likely to use W-Tap."
    );

    private final Option<Integer> tapLong = new Option<>(
            "Tap Long",
            Material.DIAMOND_LEGGINGS,
            this,
            1,
            1,
            30
    ).addDescription(
            CC.GRAY + "The duration (in ticks) for which the bot",
            CC.GRAY + "will hold the W-Tap. Higher values increase",
            CC.GRAY + "the length of time the bot resets its sprint."
    );

    private final Option<Integer> comboTapLong = new Option<>(
            "Combo Tap Long",
            Material.DIAMOND_AXE,
            this,
            1,
            1,
            30
    ).addDescription(
            CC.GRAY + "The duration (in ticks) for W-Tap when the bot",
            CC.GRAY + "is in a combo. Adjust to optimize the bot's",
            CC.GRAY + "knockback strategy during combos."
    );

    private final Option<Integer> tapStart = new Option<>(
            "Tap Start",
            Material.DIAMOND_BOOTS,
            this,
            3,
            1,
            30
    ).addDescription(
            CC.GRAY + "The number of ticks before the bot starts",
            CC.GRAY + "performing a W-Tap. This delay can help time",
            CC.GRAY + "the tap with the opponent's movement."
    );

    private final Option<Boolean> blockingHitting = new Option<>(
            "Block Hitting",
            Material.DIAMOND_SWORD,
            this,
            false,
            -1
    ).addDescription(
            CC.GRAY + "Determines if the bot will use block hitting,",
            CC.GRAY + "which involves blocking with a sword while",
            CC.GRAY + "attacking to reduce damage taken."
    );

    public WTapSettings(Category parent) {
        super(
                "wtapsettings",
                Arrays.asList(
                        "Configurable WTap",
                        "settings for the robot."
                ),
                new ItemStack(Material.DIAMOND_BOOTS),
                parent,
                "WTap Settings",
                "botgui.wtapsettings"
        );

        addChildren(enabled, chance, tapLong, comboTapLong, tapStart, blockingHitting);
    }
}
