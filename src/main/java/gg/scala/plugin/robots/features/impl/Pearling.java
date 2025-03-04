package gg.scala.plugin.robots.features.impl;

import gg.scala.plugin.robots.ScalaRobotsPlugin;
import gg.scala.plugin.robots.bot.behavior.ExecutionBehavior;
import gg.scala.plugin.robots.bot.behavior.actions.BuildingAction;
import gg.scala.plugin.robots.bot.behavior.actions.PearlingAction;
import gg.scala.plugin.robots.bot.setting.setting.RootAccessor;
import gg.scala.plugin.robots.bots.Bot;
import gg.scala.plugin.robots.features.Feature;
import me.lucko.helper.Events;
import me.lucko.helper.promise.Promise;
import me.lucko.helper.terminable.Terminable;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import net.minecraft.server.v1_8_R3.EntityEnderPearl;
import net.minecraft.server.v1_8_R3.EntityHuman;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.function.Consumer;

public class Pearling extends Feature {

    @Override
    public void tickEvent(Bot bot) {
        bot.setPearlTick(bot.getPearlTick() + 1);
        List<ItemStack> pearls = getPearls(bot);

        if (pearls.isEmpty()) return;
        if (bot.getFleeManager().getTicksSinceLastFlee() < 10) return;
        if (bot.getHealthManager().getHealth() < bot.getMovements().getTargetLocate().getTargetEntity().getHealth())
            return;
        if (getAngle(bot) < bot.getProfile().value(RootAccessor.INSTANCE.getPearlSettings().getAngleDifference()))
            return;
        if (bot.getPearlTick() < bot.getProfile().value(RootAccessor.INSTANCE.getPearlSettings().getPearlDelay()))
            return;

        bot.getActionController().runAction(
                PearlingAction.INSTANCE,
                (configuration) -> {
                    configuration.setExecutionBehavior(ExecutionBehavior.OverrideType);
                    configuration.setShouldContinueMovement(true);
                    return null;
                }
        );
    }

    public void testPearl(Bot bot) {
        pearl(getPearls(bot).get(0), CompositeTerminable.create(), bot, null);
    }

    public void pearl(ItemStack pearl, Terminable terminable, Bot bot, Consumer<Boolean> switchSlowTemp) {
        // bot.getInventoryManager().setMainHand(itemSlot(pearl, bot));

        Promise.start()
                .thenRunDelayedSync(() -> {
                    if (!bot.isAlive()) return;

                    if (getAngle(bot) < bot.getProfile().value(RootAccessor.INSTANCE.getPearlSettings().getAngleDifference())) {
                        return;
                    }

                    final Location location = bot.getMovements().getLocation();
                    bot.getMovements().rotate(
                            location.getYaw() + bot.getProfile().value(RootAccessor.INSTANCE.getPearlSettings().getPearlThrowYawOffSet()),
                            3.0F
                    );

                    bot.getMovements().getRotations().setRotationLockUntil(System.currentTimeMillis() + 1000L);
                    bot.getAttackingManager().swing();
                    bot.getInventoryManager().removeItem(itemSlot(pearl, bot));

                    launchProjectile(
                            bot.getNpc().getBukkitEntity().getHandle(),
                            bot.getMovements().getLocation().getDirection()
                    );

                    Events
                            .subscribe(ProjectileHitEvent.class)
                            .filter(event -> event.getEntity().getType().equals(EntityType.ENDER_PEARL))
                            .filter(event -> event.getEntity().getShooter() instanceof LivingEntity)
                            .filter(event -> ((LivingEntity) event.getEntity().getShooter()).getEntityId() == bot.getNpc().getBukkitEntity().getEntityId())
                            .handler(event -> {
                                var newLocation = event.getEntity().getLocation().clone();
                                newLocation.setY(newLocation.getY() + 0.1);
                                newLocation.setYaw(bot.getMovements().getLocation().getYaw());
                                newLocation.setPitch(bot.getMovements().getLocation().getPitch());

                                updateLocation(bot, newLocation);
                            });
                }, bot.getProfile().value(RootAccessor.INSTANCE.getPearlSettings().getPearlingTicks()))
                .thenRunDelayedSync(() -> {
                    switchSlowTemp.accept(true);
                    terminable.closeAndReportException();
                }, 10L)
                .bindWith(bot.getTerminable());
    }

    public EntityEnderPearl launchProjectile(EntityHuman human, Vector targetLocation) {
        EntityEnderPearl launch = new EntityEnderPearl(human.getWorld(), human);

        Validate.notNull(launch, "Projectile not supported");

        ((Projectile) launch.getBukkitEntity()).setVelocity(targetLocation);

        human.getWorld().addEntity(launch);
        return launch;
    }

    public void updateLocation(Bot bot, Location location) {
        bot.getMovements().setLocation(location);
        final double x = location.getX();
        final double y = location.getY();
        final double z = location.getZ();
        final float yaw = location.getYaw();
        final float pitch = location.getPitch();
        bot.getNpc().setLocation(x, y, z, yaw, pitch);
    }

    public int itemSlot(ItemStack stack, Bot bot) {
        return bot.getInventoryManager().getItemSlot(stack);
    }

    public float getAngle(Bot bot) {
        Location from = bot.getMovements().getLocation();
        Location to = bot.getMovements().getTargetLocate().getTargetEntity().getLocation();

        float yaw = (float) calculateYawDifference(to, from).getX();
        yaw -= to.getYaw();
        if (yaw > 180) yaw -= 360;
        return Math.abs(yaw);
    }

    public static Vector calculateYawDifference(Location from, Location to) {
        Location clonedFrom = from.clone();
        Vector startVector = clonedFrom.toVector();
        Vector targetVector = to.toVector();
        clonedFrom.setDirection(targetVector.subtract(startVector));

        return new Vector(clonedFrom.getYaw(), 0, clonedFrom.getPitch());
    }

    public List<ItemStack> getPearls(Bot bot) {
        return bot.getInventoryManager().hasItem(Material.ENDER_PEARL);
    }
}
