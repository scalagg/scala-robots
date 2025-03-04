package gg.scala.plugin.robots.utils.objects;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SomePlayerRunnable implements PlayerRunnable {
    @Override
    public void run(Player player, int pressedSlot) {
        // Perform some operations that do not throw IOException

        // If an error occurs, notify the player
        player.sendMessage(ChatColor.RED + "An error occurred while processing your request.");
    }
}
