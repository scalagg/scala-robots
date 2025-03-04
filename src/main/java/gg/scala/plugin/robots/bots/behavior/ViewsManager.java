package gg.scala.plugin.robots.bots.behavior;

import lombok.Getter;
import gg.scala.plugin.robots.bots.Bot;
import gg.scala.plugin.robots.bots.managers.Manager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ViewsManager extends Manager {
    private final List<Player> players = new ArrayList<>();

    public ViewsManager(Bot bot) {
        super(bot);
    }

    public void addPlayer(Player player) {
        players.add(player);
        getBot().getPacketsManager().sendCreatesPackets(player, 0);
        getBot().getInventoryManager().updateArmorPacket(0);
        getBot().getInventoryManager().updateArmorPacket(1);
        getBot().getInventoryManager().updateArmorPacket(2);
        getBot().getInventoryManager().updateArmorPacket(3);
        getBot().getInventoryManager().updateItemPacket(bot.getInventoryManager().getItemInMainHand());
        addPlayerToList(player);

        player.showPlayer(bot.getNpc().getBukkitEntity());
    }

    public void removePlayer(Player player) {
        if (players.contains(player)) {
            players.remove(player);
            getBot().getPacketsManager().sendRemovePackets(player);
            player.hidePlayer(bot.getNpc().getBukkitEntity());
        }
    }
}
