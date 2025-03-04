package gg.scala.plugin.robots.bots.events.impl;

import gg.scala.plugin.robots.bots.events.Event;
import gg.scala.plugin.robots.bots.Bot;

public class BotAttackEvent extends Event {

    public BotAttackEvent(Bot bot) {
        super(bot);
    }
}
