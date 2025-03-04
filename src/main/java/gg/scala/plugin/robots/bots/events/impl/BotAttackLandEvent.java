package gg.scala.plugin.robots.bots.events.impl;

import lombok.Getter;
import gg.scala.plugin.robots.bots.events.Event;
import gg.scala.plugin.robots.bots.Bot;
import org.bukkit.entity.Player;

@Getter
public class BotAttackLandEvent extends Event {
    private final Player player;

    public BotAttackLandEvent(Bot bot, Player player) {
        super(bot);
        this.player = player;
    }
}
