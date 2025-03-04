package gg.scala.plugin.robots.bots.events.impl;

import lombok.Getter;
import gg.scala.plugin.robots.bots.events.Event;
import gg.scala.plugin.robots.bots.Bot;
import org.bukkit.inventory.ItemStack;

//Also being called when finished drinking.
@Getter
public class FinishEatingEvent extends Event {

    private final ItemStack item;

    public FinishEatingEvent(Bot bot, ItemStack item) {
        super(bot);
        this.item = item;
    }
}
