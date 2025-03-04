package gg.scala.plugin.robots.bots.settings;

import gg.scala.plugin.robots.bot.setting.setting.Category;
import gg.scala.plugin.robots.bot.setting.setting.Option;
import lombok.Getter;
import lombok.Setter;
import net.evilblock.cubed.util.CC;
import net.evilblock.cubed.util.bukkit.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
@Setter
public class PotionSettings extends Category {

    //Health potion
    private Option<Integer> fleeHealth = new Option<>(
            "Flee Health",
            Material.APPLE,
            this,
            4, 1, 20
    ).addDescription(
            CC.GRAY + "The health threshold at which",
            CC.GRAY + "the bot will attempt to flee."
    );

    private Option<Boolean> agroPotion = new Option<>(
            "Agro Potion",
            Material.DIAMOND_SWORD,
            this,
            false, -1
    ).addDescription(
            CC.GRAY + "Determines whether the bot will",
            CC.GRAY + "use an aggressive potion."
    );

    private Option<Integer> firstPotionTick = new Option<>(
            "First Potion Tick",
            Material.WATCH,
            this,
            9, 1, 60
    ).addDescription(
            CC.GRAY + "The game tick at which the bot",
            CC.GRAY + "throws the first potion."
    );

    private Option<Integer> secondPotionTick = new Option<>(
            "Second Potion Tick",
            Material.WATCH,
            this,
            12, 1, 60
    ).addDescription(
            CC.GRAY + "The game tick at which the bot",
            CC.GRAY + "throws the second potion."
    );

    private Option<Integer> finishPotionTick = new Option<>(
            "Finish Potion Tick",
            Material.WATCH,
            this,
            22, 1, 60
    ).addDescription(
            CC.GRAY + "The game tick at which the bot",
            CC.GRAY + "throws the final potion."
    );

    private Option<Integer> tickOffset = new Option<>(
            "Tick Offset",
            Material.DIODE,
            this,
            1, 1, 20
    ).addDescription(
            CC.GRAY + "Offset in game ticks for potion",
            CC.GRAY + "timing adjustments."
    );

    private Option<Boolean> secondPotion = new Option<>(
            "Second Potion",
            Material.POTION,
            this,
            true, -1
    ).addDescription(
            CC.GRAY + "Determines if the bot should",
            CC.GRAY + "use a second potion."
    );

    private Option<Double> cancelPotionDistance = new Option<>(
            "Cancel Potion Distance",
            Material.HOPPER,
            this,
            3.0, 0.1, 100
    ).addDescription(
            CC.GRAY + "Distance in blocks at which the",
            CC.GRAY + "bot cancels potion throwing."
    );

    private Option<Boolean> hotbarRefillPausing = new Option<>(
            "Hotbar Refill Pausing",
            Material.CHEST,
            this,
            true, 0.0
    ).addDescription(
            CC.GRAY + "Should the bot pause to",
            CC.GRAY + "refill its hotbar?"
    );

    private Option<Integer> hotbarRefillTime = new Option<>(
            "Hotbar Refill Time",
            ItemBuilder
                    .of(Material.POTION)
                    .data((short) 16421)
                    .build(),
            this,
            2, 1, 4
    ).addDescription(
            CC.GRAY + "The amount of time, in seconds,",
            CC.GRAY + "it takes for the bot to refill",
            CC.GRAY + "its hotbar with health potions",
            CC.GRAY + "when empty."
    );

    private Option<Integer> hotbarRefillAmountRequired = new Option<>(
            "Hotbar Refill Amount",
            ItemBuilder
                    .of(Material.WATCH)
                    .build(),
            this,
            0, 1, 5
    ).addDescription(
            CC.GRAY + "The amount of potions which",
            CC.GRAY + "the bot must have in its hotbar",
            CC.GRAY + "when it decides to pause."
    );

    public PotionSettings(Category parent) {
        super(
                "potionsettings",
                Arrays.asList(
                        "Configurable potion",
                        "settings for the robot."
                ),
                new ItemStack(Material.POTION),
                parent,
                "Potion Settings",
                "botgui.potionsettings"
        );

        addChildren(
                fleeHealth, agroPotion, firstPotionTick, secondPotionTick, secondPotion, finishPotionTick, tickOffset, secondPotion,
                cancelPotionDistance,
                hotbarRefillPausing, hotbarRefillTime, hotbarRefillAmountRequired
        );
    }
}
