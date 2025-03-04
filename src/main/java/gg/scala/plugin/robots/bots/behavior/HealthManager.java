package gg.scala.plugin.robots.bots.behavior;

import lombok.Getter;
import lombok.Setter;
import gg.scala.plugin.robots.bots.events.impl.BotHealthChangeEvent;
import gg.scala.plugin.robots.bots.Bot;
import gg.scala.plugin.robots.bots.managers.Manager;
import me.lucko.helper.Schedulers;
import org.bukkit.potion.PotionEffectType;

@Getter
@Setter
public class HealthManager extends Manager {
    public HealthManager(Bot bot) {
        super(bot);
        health = maxHeath;
    }

    private double health;
    private int maxHeath = 20;
    private int naturalRegTicks;
    private int requiredNaturalRegTicks = 20;
    private float absHearts;
    private int regenTicks;
    private int fireTicks = 0;
    private boolean onFireUpdate = false;
    private boolean triggerHealthChangeEvent = false;
    private double healthChangeBefore;
    private double healthChangeAfter;
    private boolean poisoned = false;
    private int slownessPercentage = -1;

    @Override
    public void tickEarly() {
        if (!getBot().getNpc().isAlive()) {
            getBot().getNpc().dead = false;
            deathEvent();
            return;
        }
        getBot().getNpc().setHealth(Math.min((float) health, 1));
        if (health <= 0 && !getBot().isAlive()) getBot().getNpc().setHealth((float) health);

        int regenLevel = getBot().getEffectsManager().getLevel(PotionEffectType.REGENERATION);

        //Effects
        regenerationCheck(regenLevel);
        absCheck();
        fireCheck();

        if (triggerHealthChangeEvent) {
            healthChangeEvent(healthChangeBefore, healthChangeAfter);
            triggerHealthChangeEvent = false;
        }
    }

    // Checks if the bot has abs effect
    public void absCheck() {
        if (getBot().getEffectsManager().getLevel(PotionEffectType.ABSORPTION) == 0) absHearts = 0;
    }

    //Regen Effect
    public void regenerationCheck(int level) {
        if (level == 0) {
            regenTicks = 0;
            return;
        }

        int requireTicks = 50;
        if (level == 2) {
            requireTicks = 25;
        } else if (level == 3) {
            requireTicks = 12;
        } else if (level == 4) {
            requireTicks = 6;
        } else if (level == 5) {
            requireTicks = 3;
        } else if (level >= 6) {
            requireTicks = 1;
        }

        if (regenTicks++ != 0 && regenTicks % requireTicks == 0)
            addHealth(1);
    }

    //Fire check
    public void fireCheck() {
        int fireRess = getBot().getEffectsManager().getLevel(PotionEffectType.FIRE_RESISTANCE);
        fireTicks = Math.max(0, fireTicks - 1);
        if (fireTicks == 0 && onFireUpdate) getBot().getPacketsManager().setPlayerOnFirePackets(false);

        if (fireRess >= 1 || fireTicks == 0) return;
        if (fireTicks % 20 == 0) {
            bot.getDamageManager().playSound();
            bot.getDamageManager().damageEffect();
            removeHealth(1);
        }
    }

    public void removeHealth(double amount) {
        healthChangeEvent(health, health - amount);
        health -= amount;
        if (health < 0) {
            deathEvent();
            //Bukkit.broadcastMessage("d1");
        }
    }

    public void deathEvent() {
        getBot().kill();
    }

    public void healthChangeEvent(double before, double after) {
        callEventAsync(new BotHealthChangeEvent(getBot(), after, before));
    }

    public void addHealth(double amount) {
        healthChangeBefore = health;
        health = Math.min(maxHeath, health + amount);
        healthChangeAfter = health;
        triggerHealthChangeEvent = true;
    }

    public void fire(int ticks) {
        getBot().getPacketsManager().setPlayerOnFirePackets(true);
        fireTicks = ticks;
        onFireUpdate = true;
    }

    public void addPoisonTicks(int amplifier) {
        int period = amplifier == 0 ? 25 : 25 / amplifier;
        int ticks = (amplifier == 1 || amplifier == 0) ? 33 * 20 : 45 * 20;

        poisoned = true;

        Schedulers
                .sync()
                .runRepeating((task) -> {
                    if (!poisoned) {
                        task.stop();
                        return;
                    }
                    if (health > 1) {
                        bot.getDamageManager().playSound();
                        bot.getDamageManager().damageEffect();
                        removeHealth(0.5);
                    }
                }, period, period)
                .bindWith(bot.getTerminable());

        Schedulers
                .sync()
                .runLater(() -> {
                    poisoned = false;
                }, ticks)
                .bindWith(bot.getTerminable());
    }

    public void addSlownessTicks(int amplifier) {
        int percentage = amplifier == 0 ? 15 : 15 * amplifier;
        int ticks = 67;

        slownessPercentage = percentage;

        Schedulers
                .sync()
                .runLater(() -> {
                    slownessPercentage = -1;
                }, ticks)
                .bindWith(bot.getTerminable());
    }
}
