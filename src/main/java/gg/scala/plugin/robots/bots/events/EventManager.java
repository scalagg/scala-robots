package gg.scala.plugin.robots.bots.events;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public class EventManager {

    private final ArrayList<EventSubscription> events = new ArrayList<>();

    public void registerEvent(EventSubscription event) {
        events.add(event);
    }

    public void unregisterEvent(EventSubscription event) {
        events.remove(event);
    }
}
