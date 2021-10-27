package me.ofearr.ultimatefreeze.Commands;

import me.ofearr.ultimatefreeze.FreezeCore;
import me.ofearr.ultimatefreeze.UltimateFreeze;
import me.ofearr.ultimatefreeze.Utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FreezeCMD implements CommandExecutor {

    private static UltimateFreeze plugin;
    private String prefix = plugin.getConfig().getString("plugin-prefix");

    public FreezeCMD(UltimateFreeze uF){
        this.plugin = uF;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().toLowerCase().equals("freeze")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!(player.hasPermission("ultimatefreeze.freeze"))) {
                    String noPerms = plugin.getConfig().getString("insufficient-permissions-message").replace("{prefix}", prefix);
                    player.sendMessage(StringUtils.translate(noPerms));
                } else {
                    if (args.length == 0) {
                        String noArgs = plugin.getConfig().getString("missing-arguments-message").replace("{prefix}", prefix);
                        player.sendMessage(StringUtils.translate(noArgs));
                    } else {
                        if (Bukkit.getServer().getPlayer(args[0]) == null) {
                            String notOnline = plugin.getConfig().getString("not-online-message").replace("{prefix}", prefix);
                            player.sendMessage(StringUtils.translate(notOnline));
                        } else {
                            Player frozen = Bukkit.getServer().getPlayer(args[0]).getPlayer();

                            if (frozen.hasPermission("ultimatefreeze.bypass")) {
                                player.sendMessage(StringUtils.translate(plugin.getConfig().getString("cant-freeze-message").replace("{prefix}", prefix)));
                            } else {
                                if (plugin.FrozenPlayers.containsKey(frozen.getUniqueId())) {
                                    FreezeCore.unFreezePlayer(player, frozen);
                                } else {
                                    FreezeCore.freezePlayer(player, frozen);
                                }
                            }
                        }
                    }

                }
            }
        }
        return false;
    }
}
