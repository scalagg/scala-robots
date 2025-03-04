package gg.scala.plugin.robots.features.impl;

import gg.scala.plugin.robots.bots.events.Event;
import gg.scala.plugin.robots.bots.events.impl.BotHealthChangeEvent;
import gg.scala.plugin.robots.bots.Bot;
import gg.scala.plugin.robots.bots.behavior.InventoryManager;
import gg.scala.plugin.robots.features.Feature;
import me.lucko.helper.Schedulers;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Soups extends Feature {
    // SOUPS DOESNT WORK FOR BOTS NOW!
    private int soupTime = 5;
    private int soupHealth = 10;

    @Override
    public void listen(Event event) {
        if (event instanceof BotHealthChangeEvent) {
            BotHealthChangeEvent e = (BotHealthChangeEvent) event;
            if (e.getAfter() <= soupHealth) {
                Bot bot = e.getBot();
                InventoryManager manager = bot.getInventoryManager();
                List<ItemStack> soups = manager.hasItem(Material.MUSHROOM_SOUP);
                if (soups.size() <= 0) return;

                ItemStack soup = soups.get(0);
                int slot = manager.getItemSlot(soup);
                manager.setMainHand(slot);

                Schedulers
                        .sync()
                        .runLater(() -> {
                            if (!bot.isAlive()) return;
                            manager.setMainHand(1);
                            bot.getHealthManager().addHealth(6);
                            manager.removeItem(slot);
                        }, soupTime)
                        .bindWith(bot.getTerminable());
            }
        }
    }
}
