package gg.scala.plugin.robots.bots.settings;

import gg.scala.plugin.robots.bot.setting.setting.Category;
import gg.scala.plugin.robots.bot.setting.setting.Option;
import gg.scala.plugin.robots.bot.setting.setting.persistence.SettingProfile;
import lombok.Getter;
import lombok.Setter;
import net.evilblock.cubed.util.CC;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
@Setter
public class AttackSettings extends Category {

    public Option<Double> reach = new Option<>(
            "Reach",
            Material.DIAMOND_SWORD,
            this,
            2.7,
            0.1,
            5
    ).addDescription(
            CC.GRAY + "The maximum distance (in blocks) the bot",
            CC.GRAY + "can hit a player from. Adjusting this value",
            CC.GRAY + "can affect the bot's combat effectiveness."
    );

    public Option<Integer> cps = new Option<>(
            "CPS",
            Material.DIAMOND_CHESTPLATE,
            this,
            6,
            25
    ).addDescription(
            CC.GRAY + "The clicks per second (CPS) rate of the bot.",
            CC.GRAY + "This determines how fast the bot attacks.",
            CC.GRAY + "A higher CPS results in faster attacks."
    );

    public Option<Integer> swingRange = new Option<>(
            "Swing Range",
            Material.IRON_SWORD,
            this,
            6,
            20
    ).addDescription(
            CC.GRAY + "The range (in blocks) within which the bot",
            CC.GRAY + "will swing its weapon. This can be used to",
            CC.GRAY + "control the bot's combat behavior."
    );

    public Option<Double> playerHitBox = new Option<>(
            "Player Hitbox",
            Material.IRON_CHESTPLATE,
            this,
            0.3,
            0.1,
            2
    ).addDescription(
            CC.GRAY + "The size of the player's hitbox that the bot",
            CC.GRAY + "will aim for. Adjusting this value can help",
            CC.GRAY + "in fine-tuning the bot's accuracy."
    );

    public Option<Boolean> advancedReduce = new Option<>(
            "Reduce",
            Material.STICK,
            this,
            false,
            -1
    ).addDescription(
            CC.GRAY + "Toggle to enable or disable advanced reduction",
            CC.GRAY + "techniques. This setting can be used to",
            CC.GRAY + "simulate advanced player movements and strategies."
    );

    public int getDelay(SettingProfile profile) {
        int cps = profile.value(this.cps);
        return cps > 0 ? 1000 / cps : 1000;
    }

    public AttackSettings(Category parent) {
        super(
                "attacksettings",
                Arrays.asList(
                        "Configurable PvP mechanics",
                        "for the robot."
                ),
                new ItemStack(Material.DIAMOND_SWORD),
                parent,
                "Attack Settings",
                "botgui.attacksettings"
        );
        addChildren(reach, cps, swingRange, advancedReduce, playerHitBox);
    }
}

