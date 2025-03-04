package gg.scala.plugin.robots.bots.behavior;

import gg.scala.plugin.robots.bot.setting.setting.RootAccessor;
import lombok.Getter;
import lombok.Setter;
import gg.scala.plugin.robots.bots.Bot;
import gg.scala.plugin.robots.bots.managers.Manager;
import gg.scala.plugin.robots.utils.objects.DataLocation;
import me.lucko.helper.Schedulers;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.UUID;

@Getter
@Setter
public class LagCompensatorManager extends Manager {

    private NavigableMap<Long, DataLocation> locations = new TreeMap<>();

    private int maxLocations = 20;
    private UUID lastPlayer;

    @Nullable
    public Player getPlayer() {
        return getBot().getMovements().getTargetLocate().getTargetEntity();
    }

    public LagCompensatorManager(Bot bot) {
        super(bot);

        Schedulers
                .sync()
                .runRepeating(task -> {
                    if (!getBot().isAlive()) {
                        task.closeAndReportException();
                        return;
                    }

                    Player player = getPlayer();
                    if (player == null) return;
                    if (lastPlayer == null) lastPlayer = player.getUniqueId();
                    if (lastPlayer != player.getUniqueId()) {
                        locations.clear();
                    }

                    lastPlayer = player.getUniqueId();
                    movePacket(player.getLocation());
                }, 1L, 1L)
                .bindWith(getBot().getTerminable());
    }

    public void movePacket(Location location) {
        if (location.getX() == 0 && location.getY() == 0 && location.getZ() == 0)
        {
            return;
        }

        locations.put(
                System.currentTimeMillis(),
                new DataLocation(location)
        );

        if (locations.size() > maxLocations) {
            locations.pollFirstEntry();
        }
    }

    public DataLocation getPrevLocation(long time) {
        final long targetTime = System.currentTimeMillis() - time;
        final Long floorKey = locations.floorKey(targetTime);
        final Long ceilingKey = locations.ceilingKey(targetTime);

        if (floorKey == null && ceilingKey == null) {
            var player = getPlayer();
            if (player == null) {
                return null;
            }

            return new DataLocation(getPlayer().getLocation());
        }

        if (floorKey == null) {
            return locations.get(ceilingKey);
        }

        if (ceilingKey == null) {
            return locations.get(floorKey);
        }

        return Math.abs(floorKey - targetTime) < Math.abs(ceilingKey - targetTime) ?
                locations.get(floorKey) :
                locations.get(ceilingKey);
    }

    @Nullable
    public Location getClientLocation() {
        var botPing = getBot().getProfile().value(RootAccessor.INSTANCE.getConnectionSettings().getPing());
        var packetDelay = getBot().getProfile().value(RootAccessor.INSTANCE.getConnectionSettings().getConnectionPacketDelay());

        var previousLocation = getPrevLocation(botPing + packetDelay);
        if (previousLocation == null) {
            return null;
        }

        return previousLocation
                .toLocation(
                        getBot().getMovements().getLocation().getWorld()
                );
    }
}
