package me.ofearr.ultimatefreeze.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class VersionUtil {

    static String version = Bukkit.getServer().getVersion();

    public static boolean isLegacy(){
        if (version.contains("1.13")) {
            return false;
        } else if (version.contains("1.14")) {
            return false;
        } else if (version.contains("1.15")) {
            return false;
        } else if (version.contains("1.16")) {
            return false;
        } else{
            return true;
        }
    }
}
