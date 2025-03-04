package gg.scala.plugin.robots.features.impl;

import gg.scala.plugin.robots.bot.setting.setting.RootAccessor;
import gg.scala.plugin.robots.bots.events.Event;
import gg.scala.plugin.robots.bots.events.impl.BotAttackLandEvent;
import gg.scala.plugin.robots.bots.Bot;
import gg.scala.plugin.robots.features.Feature;
import gg.scala.plugin.robots.ScalaRobotsPlugin;
import me.lucko.helper.Schedulers;

import java.util.concurrent.ThreadLocalRandom;

public class WTapFeature extends Feature {

    @Override
    public void listen(Event event) {
        if (event instanceof BotAttackLandEvent) {
            BotAttackLandEvent e = (BotAttackLandEvent) event;
            Bot bot = e.getBot();
            int random = (ThreadLocalRandom.current().nextInt(100));

            bot.getMovements().setAttackSprint(false);
            if (!bot.getProfile().value(RootAccessor.INSTANCE.getWTapSettings().getEnabled())) return;
            if (random > bot.getProfile().value(RootAccessor.INSTANCE.getWTapSettings().getChance())) return;

            final long tapLong;
            if (ScalaRobotsPlugin.getInstance().getDataManager().getPlayer(e.getPlayer()).getBotCombo() > 3) {
                tapLong = bot.getProfile().value(RootAccessor.INSTANCE.getWTapSettings().getComboTapLong());
            } else {
                tapLong = bot.getProfile().value(RootAccessor.INSTANCE.getWTapSettings().getComboTapLong());
            }

            Schedulers
                    .sync()
                    .runLater(() -> {
                        if (!bot.isAlive()) return;
                        if (bot.getProfile().value(RootAccessor.INSTANCE.getWTapSettings().getBlockingHitting())) {
                            //Block hitting handling
                            bot.getPacketsManager().sendBlockHittingPackets(true);
                            bot.getMovements().setUse(true);
                        } else {
                            //W tap handing
                            bot.getMovements().setMove(false);
                        }
                        bot.getMovements().setWtap(true);

                        Schedulers
                                .sync()
                                .runLater(() -> {
                                    if (!bot.isAlive()) return;

                                    if (bot.getProfile().value(RootAccessor.INSTANCE.getWTapSettings().getBlockingHitting())) {
                                        //Block hitting handling
                                        bot.getPacketsManager().sendBlockHittingPackets(false);
                                        bot.getMovements().setUse(false);
                                    } else {
                                        //W tap handing
                                        bot.getMovements().setMove(true);
                                    }

                                    bot.getMovements().setWtap(false);
                                }, tapLong)
                                .bindWith(bot.getTerminable());
                    }, bot.getProfile().value(RootAccessor.INSTANCE.getWTapSettings().getTapStart()))
                    .bindWith(bot.getTerminable());
        }
    }
}
