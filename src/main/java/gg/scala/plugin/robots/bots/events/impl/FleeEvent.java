package gg.scala.plugin.robots.bots.events.impl;

import lombok.Getter;
import lombok.Setter;
import gg.scala.plugin.robots.bots.events.Event;
import gg.scala.plugin.robots.bots.Bot;

@Getter
@Setter
public class FleeEvent extends Event {

    private boolean forceEnd;
    private boolean shouldPause;
    private int ticks;

    public FleeEvent(Bot bot, int ticks) {
        super(bot);
        this.ticks = ticks;
    }
}
