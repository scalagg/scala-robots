package gg.scala.plugin.robots.bots.events.impl;

import lombok.Getter;
import gg.scala.plugin.robots.bots.Bot;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class BotDeathEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Bot bot;

    public BotDeathEvent(Bot bot) {
        this.bot = bot;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
