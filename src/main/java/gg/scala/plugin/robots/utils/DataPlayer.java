package gg.scala.plugin.robots.utils;

import gg.scala.plugin.robots.bot.setting.setting.RootAccessor;
import lombok.Data;
import gg.scala.plugin.robots.bots.Bot;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class DataPlayer {
    private final Player player;
    private WeakReference<Bot> targetedByRobot = null;
    private Vector motion;

    private final CompositeTerminable terminable = CompositeTerminable.create();
    private final CopyOnWriteArrayList<Vector> locations = new CopyOnWriteArrayList<>();

    public void handleFlying(PacketPlayInFlying wrapper) {
        Vector posUpdate = new Vector(wrapper.a(), wrapper.b(), wrapper.c());
        handlePos(posUpdate);
    }

    {
        Events
                .subscribe(EntityDamageByEntityEvent.class)
                .handler(event -> {
                    if (event.getEntity().getEntityId() == getPlayer().getEntityId()) {
                        Bot bot = getTargetedBy();
                        if (bot != null) {
                            bot.getAttackingManager().setAttackTicks(0);
                        }
                    }
                })
                .bindWith(terminable);
    }

    private int combo;
    private int botCombo;

    @Nullable
    public Bot getTargetedBy() {
        if (targetedByRobot == null) {
            return null;
        }

        return targetedByRobot.get();
    }

    public void botHit() {
        botCombo++;
        combo = 0;
    }

    public void hit() {
        combo++;
        botCombo = 0;
    }

    public boolean handle(@NotNull Bot bot) {
        long diff = System.currentTimeMillis() - bot.getAttackingManager().getAttacked();
        long diff2 = System.currentTimeMillis() - bot.getAttackingManager().getLastProjectile();
        bot.getAttackingManager().setLandAttack(diff < 150);
        return (diff < 150 && !bot.getDamageManager().isCombo()) || diff2 < 1000;
    }

    public Vector velocity(@NotNull Bot bot) {
        Vector dir = MathUtils.getDirection(bot.getMovements().getLocation().getYaw());
        if (bot.getAttackingManager().isProjectile())
            return dir.multiply(0.8).setY(0.4); // TODO check if the damge was handled by a projectile
        double h = 0.47;
        double v = 0.35;
        double reach = 3.5;
        double distMultiplier = 0.15;

        var wtapEnabled = bot.getProfile().value(
                RootAccessor.INSTANCE.getWTapSettings().getEnabled()
        );

        if (bot.getMovements().isAttackSprint() || !wtapEnabled) h *= 1.2;
        if (player.isOnGround()) h *= 1.1;
        double extraH = Math.min(Math.max((reach - (bot.getMovements().getLocation().distance(player.getLocation()) - 0.3)) * distMultiplier, 0), 0.4);
        h += extraH;
        double speed = 1;
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            if (potionEffect.getType().equals(PotionEffectType.SPEED)) speed = potionEffect.getAmplifier() * 0.2 + 1;
        }
        h += 0.1 * 1.3 * speed;

        dir.multiply(h);
        dir.setY(v);

        return dir;
    }

    public void handlePos(Vector pos) {
        if (pos.equals(new Vector(0, 0, 0))) return;
        locations.add(pos);

        if (locations.size() > 3) locations.remove(0);
    }
}
