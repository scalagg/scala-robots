package gg.scala.plugin.robots.bots.events.impl;

import lombok.Getter;
import gg.scala.plugin.robots.bots.events.Event;
import gg.scala.plugin.robots.bots.Bot;

@Getter
public class SpawnEvent extends Event {

    public SpawnEvent(Bot bot) {
        super(bot);
    }
}
