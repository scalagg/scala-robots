package gg.scala.plugin.robots.bots.behavior.movements.submovements;

import gg.scala.plugin.robots.ScalaRobotsPlugin;
import gg.scala.plugin.robots.bot.setting.setting.RootAccessor;
import lombok.Getter;
import lombok.Setter;
import gg.scala.plugin.robots.bots.events.impl.RotationEvent;
import gg.scala.plugin.robots.bots.Bot;
import gg.scala.plugin.robots.bots.managers.Manager;
import gg.scala.plugin.robots.bots.behavior.movements.MovementsMain;
import gg.scala.plugin.robots.utils.MathUtils;
import gg.scala.plugin.robots.utils.WorldUtils;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
public class Rotations extends Manager {
    public Rotations(Bot bot) {
        super(bot);
    }

    private Integer JitterRotation = bot.getProfile().value(RootAccessor.INSTANCE.getRotationSettings().getJitterRotation());
    private Integer rotationSpeedRandom = bot.getProfile().value(RootAccessor.INSTANCE.getRotationSettings().getSpeedRandom());

    private Location targetPos = null;

    private float lastYaw;
    private @Nullable Long rotationLockUntil;

    @Override
    public void tickEarly() {
        var data = ScalaRobotsPlugin.getInstance().getDataManager().getPlayer(bot.getMovements().getTargetLocate().getTargetEntity());
        if (data == null) {
            return;
        }

        if (rotationLockUntil != null) {
            if (System.currentTimeMillis() <= rotationLockUntil) {
                return;
            }
            rotationLockUntil = null;
        }

        if (bot.getActionController().current() != null) {
            return;
        }

        float speed = bot.getProfile().value(RootAccessor.INSTANCE.getRotationSettings().getSpeed());
        if (bot.getFleeManager().getTicksSinceLastFlee() < 5) {
            speed = bot.getProfile().value(RootAccessor.INSTANCE.getFleeSettings().getRunningRotationSpeed());
        }

        float yaw = bot.getMovements().getLocation().getYaw();
        float pitch = bot.getMovements().getLocation().getPitch();

        MovementsMain movements = bot.getMovements();
        Location targetLocation = movements.getTargetLocate().getAStarTarget() == null ?
                movements.getTargetLocate().getTarget() :
                movements.getTargetLocate().getAStarTarget();

        //Flips
        double flipYaw = 0;
        double flipPitch = 0;

        //Flip check
        boolean bool1 = bot.getAttackingManager().getAttackTicks() <= bot.getProfile().value(RootAccessor.INSTANCE.getPlayStyleSettings().getFlipLong());
        boolean bool2 = bot.getProfile().value(RootAccessor.INSTANCE.getPlayStyleSettings().getFlipSync());
        boolean bool3 = bot.getMovements().isWtap();
        boolean bool4 = data.getBotCombo() > 3; //If necessary could be made into a setting
        boolean flip = bool2 && bool3 && bool4;

        if (!bool2 && bool1 && bool4) flip = true;

        if (bot.getProfile().value(RootAccessor.INSTANCE.getPlayStyleSettings().getFlips()) && flip) {
            //Flip chance
            if (MathUtils.getRandom(0, 100) <= bot.getProfile().value(RootAccessor.INSTANCE.getPlayStyleSettings().getFlipChance())) {
                double size = 1;
                size = generateSize();

                flipYaw = bot.getProfile().value(RootAccessor.INSTANCE.getPlayStyleSettings().getFlipYaw()) * size;
                flipPitch = bot.getProfile().value(RootAccessor.INSTANCE.getPlayStyleSettings().getFlipPitch()) * size;
            }
        }

        Vector aimBot = WorldUtils.calculateDirectionDifference(movements.getLocation(), targetLocation);

        this.lastYaw = yaw;
        yaw = aimAssist(yaw, (float) (aimBot.getX() + flipYaw), speed) + (float) MathUtils.getRandom(-JitterRotation, JitterRotation);
        pitch = aimAssistPitch(pitch, (float) (aimBot.getZ() + flipPitch), speed) + (float) MathUtils.getRandom(-JitterRotation, JitterRotation);
        if (Math.abs(pitch) > 90) pitch = 90 * pitch / Math.abs(pitch);

        callEventAsync(new RotationEvent(bot, yaw, pitch, bot.getMovements().getLocation().getYaw(), bot.getMovements().getLocation().getPitch()));
        movements.rotate(yaw, pitch);
    }

    public int generateSize() {
        Random random = ThreadLocalRandom.current();
        int size = random.nextInt(2);
        if (size == 0) return -1;
        return 1;
    }

    public float aimAssistPitch(float before, float after, float s) {
        double delta = after - before;

        double speed = s + (float) MathUtils.getRandom(-rotationSpeedRandom, rotationSpeedRandom);

        if (Math.abs(delta) <= speed) return after;
        return ((float) (before + (delta / Math.abs(delta) * speed)));
    }

    public float aimAssist(float before, float after, float s) {
        double deltaYaw;

        double speed = s + (float) MathUtils.getRandom(-rotationSpeedRandom, rotationSpeedRandom);

        double original = after - before;
        float var1 = (after + 360) - before;
        float var2 = after - (before + 360);
        float var3 = (after + 360) - (before + 360);

        deltaYaw = Math.min(min(Math.abs(var1), Math.abs(var2), Math.abs(var3)), Math.abs(original));
        if (deltaYaw == Math.abs(var1)) deltaYaw = var1;
        if (deltaYaw == Math.abs(var2)) deltaYaw = var2;
        if (deltaYaw == Math.abs(var3)) deltaYaw = var3;
        if (deltaYaw == Math.abs(original)) deltaYaw = original;


        if (Math.abs(deltaYaw) > speed) return ((float) (before + (deltaYaw / Math.abs(deltaYaw) * speed)));
        return after;
    }

    public double min(double var1, double var2, double var3) {
        double var4 = Math.min(var1, var2);
        return Math.min(var3, var4);
    }
}
