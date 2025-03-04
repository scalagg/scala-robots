package gg.scala.plugin.robots.bots.events.impl;

import lombok.Getter;
import lombok.Setter;
import gg.scala.plugin.robots.bots.events.Event;
import gg.scala.plugin.robots.bots.Bot;

@Getter
@Setter
public class RotationEvent extends Event {

    private float newYaw;
    private float newPitch;

    private float oldYaw;
    private float oldPitch;

    public RotationEvent(Bot bot, float newYaw,float newPitch,float oldYaw, float oldPitch) {
        super(bot);
        this.newYaw = newYaw;
        this.newPitch = newPitch;
        this.oldPitch = oldPitch;
        this.oldYaw = oldYaw;
    }
}
