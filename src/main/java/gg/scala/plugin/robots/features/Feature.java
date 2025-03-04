package gg.scala.plugin.robots.features;

import gg.scala.plugin.robots.bots.events.EventSubscription;
import gg.scala.plugin.robots.bots.events.Event;
import gg.scala.plugin.robots.bots.events.impl.FinalMoveEvent;
import gg.scala.plugin.robots.bots.Bot;
import gg.scala.plugin.robots.ScalaRobotsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class Feature extends EventSubscription implements Listener {

    public Feature() {
        ScalaRobotsPlugin.getInstance().getEventManager().registerEvent(this);
        Bukkit.getServer().getPluginManager().registerEvents(this, ScalaRobotsPlugin.getInstance());
    }
}
