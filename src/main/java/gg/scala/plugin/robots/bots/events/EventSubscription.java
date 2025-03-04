package gg.scala.plugin.robots.bots.events;

import gg.scala.plugin.robots.bots.Bot;

public class EventSubscription {

    public void listen(Event event) {}

    public void tickEvent(Bot bot) {}

    public void call(Event event) {
        listen(event);
    }

    public void execute(Bot bot) {
        tickEvent(bot);
    }
}
