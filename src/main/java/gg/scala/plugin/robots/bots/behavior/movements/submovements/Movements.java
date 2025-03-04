package gg.scala.plugin.robots.bots.behavior.movements.submovements;

import gg.scala.plugin.robots.bot.setting.setting.RootAccessor;
import gg.scala.plugin.robots.bots.Bot;
import gg.scala.plugin.robots.bots.managers.Manager;
import gg.scala.plugin.robots.utils.BlockManager;
import gg.scala.plugin.robots.utils.RayTrace;
import gg.scala.plugin.robots.utils.objects.AABB;
import gg.scala.plugin.robots.utils.objects.CraftiFriction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Movements extends Manager {
    public Movements(Bot bot) {
        super(bot);
    }

    @Override
    public void tickEarly() {
        //boolean requireToMove = bot.getMovements().getLocation().distanceSquared(bot.getMovements().getTargetLocate().getTarget()) >=Math.pow(settingManager.getStrafeSettings().getDistance().getDoubleValue(), 2);
        //bot.getMovements().setRequiredToMove(requireToMove);
        //if (!requireToMove) bot.getAttackingManager().setHitSelecting(false);

        move();
        bot.getVelocityCalculator().setAccVelocity(new Vector(0, 0, 0));
    }

    private final double blockingMultiplier = 0.2;
    private final boolean advancedReduce = bot.getProfile().value(RootAccessor.INSTANCE.getAttackSettings().getAdvancedReduce());

    private boolean lastOnGround;

    public void move() {
        Vector motion = getDirection(bot.getMovements().getLocation().getYaw() + bot.getMovements().getStrafe(), 0);
        float friction = bot.getMovements().getFriction();

        // The default movement speed
        double movementSpeed = 0.1;
        int speed = bot.getEffectsManager().getLevel(PotionEffectType.SPEED);
        movementSpeed += 0.02 * speed;

        if (bot.getMovements().isOnGround()) {
            if (bot.getMovements().isSprint()) movementSpeed *= 1.3;
            movementSpeed *= (0.16277136f / Math.pow((friction), 3));

            int x = (int) Math.floor(bot.getMovements().getLocation().getX());
            int y = (int) (Math.floor(bot.getMovements().getLocation().getBlockY()) - 1);
            int z = (int) Math.floor(bot.getMovements().getLocation().getZ());
            Block belowBlock = bot.getNpc().getBukkitEntity().getWorld().getBlockAt(x, y, z);
            Block curBlock = bot.getNpc().getBukkitEntity().getWorld().getBlockAt(x, y + 1, z);
            Block cur2Block = bot.getNpc().getBukkitEntity().getWorld().getBlockAt(x, y + 2, z);

            if (belowBlock.isLiquid() || curBlock.isLiquid() || cur2Block.isLiquid()) {
                friction = 0.2f;
            } else if (belowBlock.getType() == Material.ICE || curBlock.getType() == Material.PACKED_ICE) {
                friction = 0.98f;
            }

            if (bot.getMovements().isJump() && bot.getMovements().getDisableJumps() <= 0) movementSpeed += 0.2;
        } else {
            movementSpeed = 0.02;
            if (bot.getMovements().isSprint()) movementSpeed = 0.026;
            bot.getMovements().setFriction(0.91f);
            motion.setY(((bot.getMovements().getMotionY() - 0.08D) * 0.9800000190734863D));
        }

        bot.getMovements().setFriction(friction);

        if (bot.getMovements().isUse()) movementSpeed *= blockingMultiplier;
        if (!bot.getMovements().isMove()) movementSpeed *= 0;

        var currentAction = bot.getActionController().current();
        if (currentAction != null) {
            if (!currentAction.getConfiguration().getShouldContinueMovement()) {
                movementSpeed *= 0;
            }
        }

        movementSpeed *= bot.getMovements().getSpeedMultiplier();

        motion.setX(motion.getX() * movementSpeed);
        motion.setZ(motion.getZ() * movementSpeed);
        boolean velocityTaken = false;

        // Velocity
        if (bot.getVelocityCalculator().isVelocityReplace()) {
            velocityTaken = true;
            Vector velocityKb = bot.getVelocityCalculator().getReplacedVelocity(motion.clone());
            bot.getVelocityCalculator().setVelocityReplace(false);

            double attackMultiplier = Math.max(bot.getMovements().getAttackMultiplier(), 0.6);

            if (advancedReduce) if (bot.getMovements().isSprint())
                velocityKb.multiply(new Vector(attackMultiplier, 1, attackMultiplier));
            motion.setY(0);
            motion.add(velocityKb);
        }
        motion.add(bot.getVelocityCalculator().getAccVelocity());

        // Jumps
        if (bot.getMovements().isJump() && bot.getMovements().isOnGround()) {
            double jumpValue = 0.42 + (bot.getEffectsManager().getLevel(PotionEffectType.JUMP) * 0.1);

            if (bot.getMovements().isSprint()) {
                float multiplier = bot.getMovements().getLocation().getYaw() * 0.017453292F;
                motion.setX(motion.getX() + (Math.cos(multiplier) * 0.2F));
                motion.setZ(motion.getZ() + (Math.cos(multiplier) * 0.2F));
            }

            motion.setY(jumpValue);
            bot.getMovements().setJump(false);
        }

        sendMove(motion.clone(), !velocityTaken);
        updateValues(motion.clone(), true);
    }

    public void sendMove(Vector motion, boolean addLastMotion) {
        bot.getMovements().move(motion, addLastMotion);
    }

    public void updateValues(Vector motion, boolean updateFriction) {
        Location loc = bot.getMovements().getLocation().toVector().subtract(new Vector(0, 0.01, 0)).toLocation(bot.getMovements().getLocation().getWorld());
        Material block = BlockManager.getBlockType(loc);

        bot.getMovements().setOnGround(onGround(loc));

        if (updateFriction) {
            boolean onGround = bot.getMovements().isOnGround();
            boolean lastOnGround = this.lastOnGround;
            this.lastOnGround = onGround;

            float friction = (CraftiFriction.getFactor(block)) * 0.91f;
            if (!lastOnGround) friction = 0.91f;

            bot.getMovements().setFriction(friction);
        }
    }

    public static boolean onGround(Location loc) {
        AABB playerBox = new AABB(new Vector(-0.29, 0.0, -0.29), new Vector(0.29, 1.8, 0.29));
        World world = loc.getWorld();
        Location min = loc.toVector().add(playerBox.getMin()).toLocation(world);
        Location max = loc.toVector().add(playerBox.getMax().clone().setY(0)).toLocation(world);

        double var1 = (loc.getY() - loc.getBlockY());
        double var2 = var1 % (1.0 / 16.0);
        double var3 = var1 % (1.0 / 16.0) - 1.0 / 16;
        double var4 = Math.abs(Math.min(var2, var3));
        return RayTrace.blockCollidesCheck(min, max);
    }

    public static Vector getDirection(float yaw, float pitch) {
        Vector result = new Vector(0, 0, 0);
        double xz = Math.cos(Math.toRadians(pitch));
        result.setX(-xz * Math.sin(Math.toRadians(yaw)));
        result.setZ(xz * Math.cos(Math.toRadians(yaw)));

        return result;
    }
}
