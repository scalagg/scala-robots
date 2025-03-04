package gg.scala.plugin.robots.bots.events.impl;

import lombok.Getter;
import gg.scala.plugin.robots.bots.events.Event;
import gg.scala.plugin.robots.bots.Bot;

@Getter
public class BotHealthChangeEvent extends Event {

    private final double after;
    private final double before;

    public BotHealthChangeEvent(Bot bot, double after, double before) {
        super(bot);
        this.after = after;
        this.before = before;
    }
}
