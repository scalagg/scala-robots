package gg.scala.plugin.robots.features.impl;

import gg.scala.plugin.robots.bot.setting.setting.RootAccessor;
import gg.scala.plugin.robots.bots.events.Event;
import gg.scala.plugin.robots.bots.events.impl.FinalMoveEvent;
import gg.scala.plugin.robots.bots.Bot;
import gg.scala.plugin.robots.features.Feature;
import gg.scala.plugin.robots.npc.nms.FixedEntityFishingHook;
import me.lucko.helper.Schedulers;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RodFeature extends Feature {


    @Override
    public void listen(Event event) {
        Bot bot = event.getBot();
        if (event instanceof FinalMoveEvent) {
            FinalMoveEvent movementEvent = (FinalMoveEvent) event;
            try {
                double distance = movementEvent.getTo().distance(movementEvent.getBot().getMovements().getTargetLocate().getTarget());
                ItemStack mainHand = movementEvent.getBot().getInventoryManager().getItemInMainHand();

                if (!bot.getProfile().value(RootAccessor.INSTANCE.getRodSettings().getEnabled())) return;

                if (distance >= bot.getProfile().value(RootAccessor.INSTANCE.getRodSettings().getMinRodDistance()) && distance <= bot.getProfile().value(RootAccessor.INSTANCE.getRodSettings().getMaxRodDistance()) && !movementEvent.getBot().getFleeManager().isActive()) {
                    if (mainHand == null) return;
                    if (mainHand.getType().equals(Material.FISHING_ROD)) return;
                    if (!bot.getProfile().value(RootAccessor.INSTANCE.getRodSettings().getRodWhileInCombo()) && movementEvent.getBot().getCombo() > 2 && movementEvent.getBot().getDamageManager().getDamageTicks() < 10) return;
                    List<ItemStack> rods = movementEvent.getBot().getInventoryManager().hasItem(Material.FISHING_ROD);
                    if (rods.isEmpty()) return;

                    //Hold the fishingRod
                    movementEvent.getBot().setPrevSlot(movementEvent.getBot().getInventoryManager().getMainHand());
                    movementEvent.getBot().setLastSwitch(System.currentTimeMillis());
                    movementEvent.getBot().getInventoryManager().setMainHand(movementEvent.getBot().getInventoryManager().getItemSlot(rods.get(0)));
                    movementEvent.getBot().getNpc().dead = false;

                    Schedulers
                            .sync()
                            .run(() -> {
                                EntityHuman npc = movementEvent.getBot().getNpc();
                                if (npc != null) {
                                    if (!BuildingUtilitiesKt.isInLineOfSight(
                                            npc.getBukkitEntity().getLocation().getDirection(),
                                            npc.getBukkitEntity().getLocation(),
                                            movementEvent.getBot().getMovements()
                                                    .getTargetLocate()
                                                    .getTargetEntity()
                                                    .getLocation(),
                                            40.0
                                    )) {
                                        return;
                                    }

                                    launchProjectile(
                                            npc,
                                            npc.getBukkitEntity().getLocation(),
                                            movementEvent.getBot().getMovements()
                                                    .getTargetLocate()
                                                    .getTargetEntity()
                                                    .getLocation()
                                    );

                                    movementEvent.getBot().getAttackingManager().updateProjectile(); // Special velocity for the projectile
                                }
                            })
                            .bindWith(bot.getTerminable());
                } else {
                    if (System.currentTimeMillis() - movementEvent.getBot().getLastSwitch() < bot.getProfile().value(RootAccessor.INSTANCE.getRodSettings().getRodDuration())) return;
                    if (mainHand == null) return;
                    if (!mainHand.getType().equals(Material.FISHING_ROD)) return;
                    movementEvent.getBot().getInventoryManager().setMainHand(movementEvent.getBot().getPrevSlot());
                }

            } catch (IllegalArgumentException ignore) {

            }
        }
    }

    public FixedEntityFishingHook launchProjectile(EntityHuman human, Location shooterLocation, Location targetLocation) {
        FixedEntityFishingHook launch = new FixedEntityFishingHook(human.getWorld(), human);

        Validate.notNull(launch, "Projectile not supported");

        ((Projectile) launch.getBukkitEntity()).setVelocity(RodUtilitiesKt.getFishingRodLaunchVelocity(shooterLocation, targetLocation, 1.2));

        human.getWorld().addEntity(launch);
        return launch;
    }
}
