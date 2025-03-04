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
public class ConnectionSettings extends Category {

    public Option<Integer> ping = new Option<>(
            "Ping",
            Material.STRING,
            this,
            100,
            5,
            1000
    ).addDescription(
            CC.GRAY + "The simulated ping for the bot, in milliseconds."
    );

    public Option<Boolean> delayedBotLocation = new Option<>(
            "Delayed Bot Location",
            Material.WATCH,
            this,
            true,
            -1
    ).addDescription(
            CC.GRAY + "Whether the bot's location is delayed",
            CC.GRAY + "to simulate network latency."
    );

    public Option<Integer> connectionPacketDelay = new Option<>(
            "Rel-Move Simulation Ticks",
            Material.SPIDER_EYE,
            this,
            3,
            1,
            20
    ).addDescription(
            CC.GRAY + "The delay in ticks (20 ticks = 1 second)",
            CC.GRAY + "for relative movement simulation."
    );

    public ConnectionSettings(Category parent) {
        super(
                "connectionsettings",
                Arrays.asList(
                        "Configurable connection",
                        "settings for the robot."
                ),
                new ItemStack(Material.DIODE),
                parent,
                "Connection Settings",
                "botgui.connectionsettings"
        );
        addChildren(ping, delayedBotLocation, connectionPacketDelay);
    }
}

