package gg.scala.plugin.robots.features.impl;

import gg.scala.plugin.robots.bot.setting.setting.RootAccessor;
import gg.scala.plugin.robots.bots.events.Event;
import gg.scala.plugin.robots.bots.events.impl.BotHealthChangeEvent;
import gg.scala.plugin.robots.bots.events.impl.FleeEvent;
import gg.scala.plugin.robots.bots.Bot;
import gg.scala.plugin.robots.features.Feature;
import me.lucko.helper.promise.Promise;
import me.lucko.helper.promise.ThreadContext;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HealthPotionThrowing extends Feature {

    @Override
    public void listen(Event event) {
        if (event instanceof BotHealthChangeEvent e) {
            Bot bot = e.getBot();

            //Health & agro checks
            if (e.getAfter() > 4 || bot.getHealthManager().getHealth() > bot.getMovements().getTargetLocate().getTargetEntity().getHealth())
                return;

            //Getting the potions
            List<ItemStack> potions = getPotions(bot);
            if (potions.isEmpty()) return;

            //Set running
            bot.getFleeManager().setFleeing(true);

            //Set holding
            bot.getInventoryManager().setMainHand(bot.getInventoryManager().getItemSlot(potions.get(0)));
        }

        if (event instanceof FleeEvent fleeEvent) {
            splash(fleeEvent.getBot(), fleeEvent.getTicks(), () -> {
                fleeEvent.setForceEnd(true);
                fleeEvent.getBot().getFleeManager().setFleeing(false);
            }, true, () -> {
                fleeEvent.setShouldPause(true);
            });
        }
    }

    public void splash(Bot bot, int ticks, Runnable returnToPlayer, boolean shouldPause, Runnable pause) {
        //Throwing
        var firstPotionTime = ticks == bot.getProfile().value(RootAccessor.INSTANCE.getPotionSettings().getFirstPotionTick());
        var secondPotionTime = (ticks == bot.getProfile().value(RootAccessor.INSTANCE.getPotionSettings().getSecondPotionTick()) &&
                bot.getProfile().value(RootAccessor.INSTANCE.getPotionSettings().getSecondPotion()));

        //Cancel
        if (
                bot.getHealthManager().getHealth() > bot.getProfile().value(RootAccessor.INSTANCE.getPotionSettings().getFleeHealth()) && nearTarget(bot) ||
                        ticks > bot.getProfile().value(RootAccessor.INSTANCE.getPotionSettings().getFinishPotionTick())
        ) {
            bot.getInventoryManager().setMainHand(1);
            returnToPlayer.run();
            return;
        }

        if (firstPotionTime || secondPotionTime) {
            List<ItemStack> potions = getPotions(bot);
            var available = potions.get(0);
            bot.getInventoryManager().removeItem(
                    bot.getInventoryManager()
                            .getItemSlot(available)
            );

            launchProjectilePotion(bot, available);

            // Set holding
            if (potions.size() > 1) {
                if (shouldPause) {
                    var refillTime = bot.getProfile().value(RootAccessor.INSTANCE.getPotionSettings().getHotbarRefillPausing()) ?
                            bot.getProfile().value(RootAccessor.INSTANCE.getPotionSettings().getHotbarRefillTime()) : 0;
                    if (refillTime != 0) {
                        bot.setPotionsThrown(bot.getPotionsThrown() + 1);

                        var requiredPotionsLeft = bot.getProfile().value(RootAccessor.INSTANCE.getPotionSettings().getHotbarRefillAmountRequired());
                        var availableHotbarPotions = 6 - bot.getPotionsThrown();

                        if (availableHotbarPotions == requiredPotionsLeft) {
                            pauseRobotForRefill(bot, refillTime);
                            pause.run();

                            bot.setPotionsThrown(0);
                            return;
                        }
                    }
                }

                bot.getInventoryManager().setMainHand(
                        bot.getInventoryManager().getItemSlot(potions.get(1))
                );
            } else {
                // We don't have any more potions, so we'll just cancel the flee
                bot.getInventoryManager().setMainHand(1);
                returnToPlayer.run();
            }
        }
    }

    private void pauseRobotForRefill(Bot bot, Integer timeInSeconds) {
        Promise.start()
                .thenRunDelayed(ThreadContext.ASYNC, () -> {
                    bot.getMovements().setMove(false);
                }, 20L)
                .thenRunDelayed(ThreadContext.ASYNC, () -> {
                    bot.getMovements().setMove(true);
                    bot.getFleeManager().setPaused(false);
                }, timeInSeconds, TimeUnit.SECONDS)
                .bindWith(bot.getTerminable());
    }

    private void launchProjectilePotion(@NotNull Bot bot, @NotNull ItemStack itemStack) {
        var direction = bot.getNpc().getBukkitEntity().getLocation().getDirection();

        launchPotion(
                itemStack,
                bot.getNpc().getBukkitEntity(),
                direction
        );
    }

    // Gets all the splash potions of a specific type
    public List<ItemStack> getPotions(Bot bot) {
        List<ItemStack> potions = bot.getInventoryManager().hasItem(373);
        List<ItemStack> result = new ArrayList<>();

        for (ItemStack item : potions) {
            if (Potion.fromItemStack(item).isSplash() && Potion.fromItemStack(item).getType().equals(PotionType.INSTANT_HEAL))
                result.add(item);
        }

        return result;
    }

    //Checks if a player is close by
    public boolean nearTarget(Bot bot) {
        Location target = bot.getLagCompensatorManager().getClientLocation();
        if (target == null) {
            return false;
        }
        Location loc = bot.getMovements().getLocation();
        double distance = target.distance(loc);

        return distance < bot.getProfile().value(RootAccessor.INSTANCE.getPotionSettings().getCancelPotionDistance());
    }

    /**
     * Responsible for scaling the thrown potion velocity to match the current
     * speed of the entity, so that the potion doesn't go flying forward and more
     * so splashes near the entity, even if the entity is running.
     */
    public static final double CATCH_UP_SCALING = 0.38;

    //Modified version of BukkitEntity.launchProjectile(Class<? extends T> projectile, Vector velocity).
    public void launchPotion(ItemStack item, CraftPlayer entity, Vector direction) {
        World world = ((CraftWorld) entity.getWorld()).getHandle();
        net.minecraft.server.v1_8_R3.Entity launch;
        launch = new EntityPotion(world, entity.getHandle(), CraftItemStack.asNMSCopy(item));

        Validate.notNull(launch, "Projectile not supported");
        var entityCatchUpScaledVelocity = direction.clone().multiply(CATCH_UP_SCALING);

        ((Projectile) launch.getBukkitEntity()).setVelocity(entityCatchUpScaledVelocity);

        world.addEntity(launch);
    }
}
