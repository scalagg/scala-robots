package gg.scala.plugin.robots.bots.behavior.movements.submovements;

import gg.scala.plugin.robots.bot.setting.setting.RootAccessor;
import lombok.Getter;
import lombok.Setter;
import gg.scala.plugin.robots.bots.Bot;
import gg.scala.plugin.robots.bots.managers.Manager;
import gg.scala.plugin.robots.utils.BlockManager;
import gg.scala.plugin.robots.utils.MathUtils;
import gg.scala.plugin.robots.utils.RayTrace;
import gg.scala.plugin.robots.utils.objects.AABB;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class Collision extends Manager {

    public Collision(Bot bot) {
        super(bot);
    }

    private final double raytraceAccuracy = 0.005;
    private Location lastValidLoc;
    private final double xzHitBoxOffset = bot.getProfile().value(RootAccessor.INSTANCE.getAttackSettings().getPlayerHitBox());
    private final AABB playerBox = new AABB(new Vector(-xzHitBoxOffset,0.0,-xzHitBoxOffset),new Vector(xzHitBoxOffset,1.8,xzHitBoxOffset));
    private boolean inWater = false;
    private List<Location> pastLocations = new ArrayList<>();
    private Runnable waterEnterEvent;
    private int stuckTicks;

    // TODO: Make it so instead of getting water, it grabs arena Loc2 Y coordinate, and if the Bot is 2 blocks below Y coordinate
    // handle death event.
    public void runWaterCheck() {
        inWater = collidesCheck(bot.getMovements().getLocation(),Material.WATER,Material.STATIONARY_WATER,Material.WATER_LILY);
        if (waterEnterEvent != null && inWater) waterEnterEvent.run();
    }

    public Location cast(Location from, Location to) {
        //Will not check for distances greater than 100 to prevent issues as the function is not intended for long distances.
        if (Math.abs(from.distance(to)) > 100) to = from.clone();
        RayTrace trace = new RayTrace(from.clone());

        Location yTo = to.clone();
        Location yToClone = yTo.clone();
        Location xzTo = to.clone();
        yTo.setX(from.getX());
        yTo.setZ(from.getZ());
        xzTo.setY(from.getY());
        Location xzToClone = xzTo.clone();

        Location result1 = trace.traceWithBlocks(playerBox,yTo,raytraceAccuracy).toLocation(to.getWorld());
        Location result2 = trace.traceWithBlocks(playerBox,xzTo,raytraceAccuracy).toLocation(to.getWorld());

        //Jumps
        if ((result2.getX() != xzToClone.getX() || result2.getZ() != xzToClone.getZ()) && result1.getY() == yToClone.getY() && bot.getMovements().isOnGround() && !collidesCheckSolid(xzToClone.clone().add(0,1.05,0))) {
            bot.getMovements().setJump(true);
        }
        result1.setX(result2.getX());
        result1.setZ(result2.getZ());
        Location result3 = trace.traceWithBlocks(playerBox,result1,raytraceAccuracy).toLocation(to.getWorld());


        // Calculate the stuck ticks
        if (result3.distance(from) < 0.02 && bot.getMovements().isMove())
            stuckTicks++;
        else stuckTicks = 0;

        if (stuckTicks == 0) {
            pastLocations.add(result3);
            if (pastLocations.size() > 5) {
                pastLocations.remove(0);
            }
        }

        if (stuckTicks > 3) {
            //Set back the bot
            bot.getMovements().setDisableJumps(5);
            return pastLocations.get(0);
        }

        return result3;
    }

    public AABB getHitBox(Location location) {
        //Player hit box
        Vector min = location.clone().toVector().add(new Vector(-0.3,0,-0.3));
        Vector max = location.clone().toVector().add(new Vector(0.3,1.8,0.3));

        return new AABB(min,max);
    }

    public boolean collidesCheck(Location location,Material... type) {
        World world = location.getWorld();
        //Player hit box
        Location min = location.clone().toVector().add(new Vector(-0.3,0,-0.3)).toLocation(world);
        Location max = location.clone().toVector().add(new Vector(0.3,1,0.3)).toLocation(world);

        return collides(min, max,type);
    }



    public boolean collides(Location min,Location max,Material... type) {
        List<Material> list = Arrays.asList(type);
        for (Block block : MathUtils.blocksFromTwoPoints(min,max)) {
            if (list.contains(block.getType())) return true;
        }

        return false;
    }

    public boolean collidesCheckSolid(Location location) {
        World world = location.getWorld();
        //Player hit box
        Location min = location.clone().toVector().add(new Vector(-0.3,0,-0.3)).toLocation(world);
        Location max = location.clone().toVector().add(new Vector(0.3,1,0.3)).toLocation(world);

        return collidesSolid(min, max);
    }



    public boolean collidesSolid(Location min,Location max) {
        for (Block block : MathUtils.blocksFromTwoPoints(min,max)) {
            AABB bHitBox = BlockManager.blockHitBox(block);
            //Checks if the block hitbox collides with the given hitbox.
            if (bHitBox.isColliding(new AABB(min.toVector(),max.toVector())) && block.getType().isSolid()) return true;
        }

        return false;
    }
}
