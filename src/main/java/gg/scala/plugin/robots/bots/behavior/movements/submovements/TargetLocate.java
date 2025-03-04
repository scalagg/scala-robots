package gg.scala.plugin.robots.bots.behavior.movements.submovements;

import gg.scala.plugin.robots.ScalaRobotsPlugin;
import gg.scala.plugin.robots.bot.UtilitiesKt;
import lombok.Getter;
import lombok.Setter;
import gg.scala.plugin.robots.bots.Bot;
import gg.scala.plugin.robots.bots.managers.Manager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

@Getter
@Setter
public class TargetLocate extends Manager {
    private Location target;
    private boolean overwriteLocation;
    private Player targetEntity;

    private boolean delayedPlayerLocation = true;
    private Location aStarTarget = null;

    public TargetLocate(Bot bot) {
        super(bot);
        this.overwriteLocation = true;
    }

    @Override
    public boolean earlyShouldContinue() {
        var previousTarget = this.targetEntity;
        this.targetEntity = UtilitiesKt.getTargetEntity(bot);

        if (previousTarget != targetEntity) {
            if (previousTarget != null) {
                var previousData = ScalaRobotsPlugin.getInstance().getDataManager().getPlayer(previousTarget);
                if (previousData != null) {
                    previousData.setTargetedByRobot(null);
                }
            }

            if (targetEntity != null) {
                var newTargetData = ScalaRobotsPlugin.getInstance().getDataManager().getPlayer(targetEntity);
                if (newTargetData != null) {
                    newTargetData.setTargetedByRobot(new WeakReference<>(bot));
                }
            }
        }

        if (overwriteLocation) {
            target = bot.getLagCompensatorManager().getClientLocation();
        }

        if (!delayedPlayerLocation && overwriteLocation) {
            if (targetEntity != null) {
                target = targetEntity.getLocation();
            }
        }
        return targetEntity != null;
    }
}
