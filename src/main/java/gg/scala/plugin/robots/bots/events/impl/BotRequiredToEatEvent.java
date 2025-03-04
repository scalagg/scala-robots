package gg.scala.plugin.robots.bots.events.impl;

import gg.scala.plugin.robots.bots.events.Event;
import gg.scala.plugin.robots.bots.Bot;

public class BotRequiredToEatEvent extends Event {

    public BotRequiredToEatEvent(Bot bot) {
        super(bot);
    }
}
