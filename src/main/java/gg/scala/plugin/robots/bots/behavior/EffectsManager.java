package gg.scala.plugin.robots.bots.behavior;

import lombok.Getter;
import gg.scala.plugin.robots.bots.events.impl.EffectEndEvent;
import gg.scala.plugin.robots.bots.Bot;
import gg.scala.plugin.robots.bots.managers.Manager;
import me.lucko.helper.Schedulers;
import net.minecraft.server.v1_8_R3.MobEffect;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class EffectsManager extends Manager {

    private final Map<PotionEffectType, Integer> effects = new ConcurrentHashMap<>();
    private final Bot bot;

    public EffectsManager(Bot bot) {
        super(bot);
        this.bot = bot;
    }

    public void updateEffects() {
        Schedulers.sync()
                .run(() -> effects.keySet().forEach(effect -> {
                    bot.getNpc().addEffect(new MobEffect(
                            effect.getId(),
                            100,
                            effects.get(effect),
                            true,
                            true
                    ));
                }))
                .bindWith(bot.getTerminable());
    }

    public void addEffect(PotionEffectType type, Integer level) {
        effects.put(type, level);
        addEffectEvent(type, level, true);
    }

    public void removeEffect(PotionEffectType type) {
        effects.put(type, 0);
        bot.getMovements().callEventAsync(new EffectEndEvent(bot, type));
        updateEffects();
    }

    public void addEffect(PotionEffectType type, Integer level, int time, boolean update) {
        effects.put(type, level);

        Schedulers
                .sync()
                .runLater(() -> {
                    if (!bot.isAlive()) return;
                    removeEffect(type);
                }, time / 20)
                .bindWith(bot.getTerminable());

        addEffectEvent(type, level, update);
    }

    public int getLevel(PotionEffectType type) {
        if (!effects.containsKey(type)) return 0;
        return effects.get(type);
    }

    public void addEffects(Collection<PotionEffect> effects) {
        for (PotionEffect effect : effects) {
            addEffect(effect.getType(), effect.getAmplifier() + 1, effect.getDuration() * 20, false);
        }
        updateEffects();
    }

    public boolean hasEffects(Collection<PotionEffect> effects) {
        for (PotionEffect effect : effects) {
            if (!this.effects.containsKey(effect.getType())) return false;
            if (this.effects.get(effect.getType()) == 0) return false;
        }

        return true;
    }

    public void addEffectEvent(PotionEffectType type, int level, boolean update) {
        if (type == PotionEffectType.ABSORPTION) {
            int extraHealth = 4 * level;
            bot.getHealthManager().setAbsHearts(extraHealth);
        }
        if (update) updateEffects();
    }
}
