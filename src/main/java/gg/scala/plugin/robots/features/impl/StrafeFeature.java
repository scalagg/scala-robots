package gg.scala.plugin.robots.features.impl;

import gg.scala.plugin.robots.bot.setting.setting.RootAccessor;
import lombok.Getter;
import gg.scala.plugin.robots.bots.events.Event;
import gg.scala.plugin.robots.bots.events.impl.BotDamageEvent;
import gg.scala.plugin.robots.bots.Bot;
import gg.scala.plugin.robots.bots.events.impl.BotAttackEvent;
import gg.scala.plugin.robots.features.Feature;
import me.lucko.helper.Schedulers;
import org.bukkit.Location;

import java.util.concurrent.ThreadLocalRandom;

@Getter
public class StrafeFeature extends Feature {
    private static final int SIX_BLOCKS = 36;
    @Override
    public void tickEvent(Bot bot) {
        //Values
        boolean inCombo = bot.getCombo() > 3;

        // Check if the bot and target are in the same world
        Location botLocation = bot.getMovements().getLocation();
        Location targetLocation = bot.getMovements().getTargetLocate().getTargetEntity().getLocation();
        if (!botLocation.getWorld().equals(targetLocation.getWorld())) {
            return;
        }

        boolean close = botLocation.distanceSquared(targetLocation) < SIX_BLOCKS;
        boolean chance = ThreadLocalRandom.current().nextInt(100) <= bot.getProfile().value(RootAccessor.INSTANCE.getStrafeSettings().getChance());

        //Enable & Combo & Close check
        if (!bot.getProfile().value(RootAccessor.INSTANCE.getStrafeSettings().getEnabled()) ||
                (inCombo && !bot.getProfile().value(RootAccessor.INSTANCE.getStrafeSettings().getStrafeInCombo()))
                || !close || bot.getMovements().isEdge()
        ) {
            bot.getMovements().setStrafe(0);
            return;
        }

        if (!chance) {
            bot.getMovements().setStrafe(0);
            return;
        }

        // Defaults + ticking
        if (bot.getMovements().getStrafe() == 0) bot.getMovements().setStrafe(45f);

        int currentTick = bot.getTicks() + 1;
        bot.setTicks(bot.getTicks() + 1);

        // Switch check
        if (currentTick > bot.getProfile().value(RootAccessor.INSTANCE.getStrafeSettings().getSwitchSideTicks())) {
            bot.setTicks(0);
            bot.getMovements().setStrafe(bot.getMovements().getStrafe() * -1f);
            bot.getMovements().setSpeedMultiplier(1 / 1.05);
        }
    }

    @Override
    public void listen(Event event) {
        Bot bot = event.getBot();
        if (event instanceof BotDamageEvent) {
            int currentCombo = bot.getCombo() + 1;
            bot.setCombo(currentCombo);
            if (currentCombo > bot.getProfile().value(RootAccessor.INSTANCE.getFleeSettings().getRunFromCombosHits()) && bot.getProfile().value(RootAccessor.INSTANCE.getFleeSettings().getRunFromCombos()) && !event.getBot().getFleeManager().isFleeing()) {
                bot.getFleeManager().setFleeing(true);
                bot.setCombo(0);

                var time = bot.getProfile().value(RootAccessor.INSTANCE.getFleeSettings().getRunFromCombosTime());
                Schedulers.sync()
                          .runLater(() -> bot.getFleeManager().setFleeing(false), time)
                          .bindWith(bot.getTerminable());
            }
        }
        if (event instanceof BotAttackEvent) {
            bot.setCombo(0);
        }
    }
}
