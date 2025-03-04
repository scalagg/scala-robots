package gg.scala.plugin.robots.utils;

import org.bukkit.ChatColor;

public class Color {
    public static String code(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
