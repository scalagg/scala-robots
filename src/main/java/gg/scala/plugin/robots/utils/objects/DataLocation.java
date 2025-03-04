package gg.scala.plugin.robots.utils.objects;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
public class DataLocation {

    private final double x;
    private final double y;
    private final double z;
    private final long time;

    public DataLocation(double x,double y,double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.time = System.currentTimeMillis();
    }

    public DataLocation(Location loc) {
        this(loc.getX(),loc.getY(),loc.getZ());
    }

    public DataLocation(double x,double y,double z,long time) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.time = time;
    }

    public Location toLocation(World world) {
        return new Location(world,x,y,z);
    }
}
