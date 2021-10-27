package me.ofearr.ultimatefreeze.Commands;

import me.ofearr.ultimatefreeze.Inventories.FrozenPlayerInventory;
import me.ofearr.ultimatefreeze.Inventories.LegacyFrozenPlayerInventory;
import me.ofearr.ultimatefreeze.Utils.VersionUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FrozenPlayersCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().toLowerCase().equals("frozenlist")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!(player.hasPermission("ultimatefreeze.frozenlist"))) {
                    player.sendMessage(ChatColor.RED + "Insufficent permission!");
                } else {
                    if(VersionUtil.isLegacy()){
                        player.openInventory(new LegacyFrozenPlayerInventory().GUI());
                    } else {
                        player.openInventory(new FrozenPlayerInventory().GUI());
                    }
                }
            }

        }
        return false;
    }
}
