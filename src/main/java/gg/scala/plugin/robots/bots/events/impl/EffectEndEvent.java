package gg.scala.plugin.robots.bots.events.impl;

import lombok.Getter;
import gg.scala.plugin.robots.bots.events.Event;
import gg.scala.plugin.robots.bots.Bot;
import org.bukkit.potion.PotionEffectType;

@Getter
public class EffectEndEvent extends Event {

    private final PotionEffectType type;

    public EffectEndEvent(Bot bot,PotionEffectType type) {
        super(bot);
        this.type = type;
    }
}
