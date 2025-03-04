package gg.scala.plugin.robots.bots.events.impl;

import lombok.Getter;
import lombok.Setter;
import gg.scala.plugin.robots.bots.events.Event;
import gg.scala.plugin.robots.bots.Bot;
import org.bukkit.Location;

@Getter
@Setter
public class FinalMoveEvent extends Event {

    private Location to;
    private Location from;

    public FinalMoveEvent(Bot bot, Location to, Location from) {
        super(bot);
        this.to = to;
        this.from = from;
    }
}
