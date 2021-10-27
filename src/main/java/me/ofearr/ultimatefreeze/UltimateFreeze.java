package me.ofearr.ultimatefreeze;

import me.ofearr.ultimatefreeze.Commands.FreezeCMD;
import me.ofearr.ultimatefreeze.Commands.FrozenPlayersCMD;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.UUID;

public final class UltimateFreeze extends JavaPlugin {

    public static UltimateFreeze plugin;
    //Key Player | Value Staff
    public static HashMap<UUID,UUID> FrozenPlayers = new HashMap<>();
    //Key Player | Value time frozen (String)
    public static HashMap<UUID, String> FreezeTime = new HashMap<>();

    public static String converter(String msg) {
        String converted = ChatColor.translateAlternateColorCodes('&', msg);
        return converted;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        Bukkit.getServer().getPluginManager().registerEvents(new FreezeCore(this), this);
        getCommand("freeze").setExecutor(new FreezeCMD(this));
        getCommand("frozenlist").setExecutor(new FrozenPlayersCMD());
        loadConfig();

    }

    public void loadConfig() {
        saveDefaultConfig();
    }

}
