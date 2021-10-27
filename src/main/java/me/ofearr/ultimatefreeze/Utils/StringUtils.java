package me.ofearr.ultimatefreeze.Utils;

import org.bukkit.ChatColor;

public class StringUtils {

    public static String translate(String msg) {
        String converted = ChatColor.translateAlternateColorCodes('&', msg);
        return converted;
    }
}
