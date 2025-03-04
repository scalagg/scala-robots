package gg.scala.plugin.robots.bots.managers;

import gg.scala.plugin.robots.ScalaRobotsPlugin;
import gg.scala.plugin.robots.bots.events.EventSubscription;
import gg.scala.plugin.robots.bots.Bot;

import java.util.ArrayList;
import java.util.List;

public class ManagerService {

    private final Bot bot;
    private final ArrayList<Manager> managers = new ArrayList<>();

    public ManagerService(Bot bot) {
        this.bot = bot;
    }

    public void addManager(Manager manager) {
        managers.add(manager);
    }

    public boolean ensureReady() {
        for (Manager manager : managers) {
            if (!manager.earlyShouldContinue()) {
                return false;
            }
        }

        return true;
    }

    public void queueUpdate() {
        try {
            managers.forEach(Manager::queueUpdate);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void tickEarly() {
        try {
            managers.forEach(Manager::tickEarly);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void tickLate() {
        try {
            managers.forEach(Manager::tickLate);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void tick() {
        try {
            managers.forEach(Manager::tick);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void executeEvents() {
        List<EventSubscription> eventSubscriptions = ScalaRobotsPlugin
                .getInstance()
                .getEventManager()
                .getEvents();

        for (EventSubscription event : eventSubscriptions) {
            event.execute(bot);
        }
    }
}
