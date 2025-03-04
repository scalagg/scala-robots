package gg.scala.plugin.robots.bots.behavior;

import gg.scala.plugin.robots.bot.setting.setting.RootAccessor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import gg.scala.plugin.robots.bots.events.impl.FleeEvent;
import gg.scala.plugin.robots.bots.Bot;
import gg.scala.plugin.robots.bots.managers.Manager;
import gg.scala.plugin.robots.bots.behavior.movements.submovements.TargetLocate;
import org.bukkit.Location;
import org.bukkit.util.Vector;

@Getter
@Setter
public class FleeManager extends Manager {

    private final double clcBlocks = getBot().getProfile().value(RootAccessor.INSTANCE.getFleeSettings().getCalculatedDistance());

    private boolean active;
    @Setter(AccessLevel.PRIVATE) private int ticks;
    @Setter(AccessLevel.PRIVATE) private int ticksSinceLastFlee;

    private boolean paused = false;
    private boolean fleeing;

    public FleeManager(Bot bot) {
        super(bot);
    }

    //TODO if blocks in way

    @Override
    public void tickEarly() {
        //Active
        TargetLocate finder = getBot().getMovements().getTargetLocate();
        active = check();

        //Ticks system
        if (!paused) {
            if (active) {
                ticks++;
                ticksSinceLastFlee = 0;
            }

            if (!active) {
                ticks = 0;
                ticksSinceLastFlee++;
            }
        } else {
            return;
        }

        //Event
        if (fleeing) {
            FleeEvent fleeEvent = new FleeEvent(getBot(), ticks);
            callEvent(fleeEvent);

            if (fleeEvent.isShouldPause()) {
                paused = true;
            } else {
                if (fleeEvent.isForceEnd()) {
                    fleeing = false;
                    active = false;
                }
            }
        }

        //Running system
        finder.setOverwriteLocation(!active);
        if (!active) return;

        Vector direction = getBot().getMovements().getLocation().toVector().subtract(finder.getTargetEntity().getLocation().toVector()).setY(0).normalize();
        Location oppositeLoc = direction.multiply(clcBlocks).add(getBot().getMovements().getLocation().toVector()).toLocation(getBot().getMovements().getLocation().getWorld());

        finder.setTarget(oppositeLoc);
    }

    public boolean check() {
        return getBot().getInventoryManager().isRequiredToEat() ||
                fleeing || getBot().getInventoryManager().isEating();
    }
}
