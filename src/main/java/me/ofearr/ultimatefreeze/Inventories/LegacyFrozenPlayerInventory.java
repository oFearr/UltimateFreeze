package me.ofearr.ultimatefreeze.Inventories;

import me.ofearr.ultimatefreeze.UltimateFreeze;
import me.ofearr.ultimatefreeze.Utils.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.UUID;

public class LegacyFrozenPlayerInventory {

    public Inventory GUI(){
        Inventory FPGUI = Bukkit.createInventory(null, 54, StringUtils.translate("&c&lFrozen Players"));
        ItemStack Surrounding = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"), 1, (short) 11);
        ItemMeta s_meta = Surrounding.getItemMeta();
        s_meta.setDisplayName(ChatColor.RED + "Frozen Players");
        Surrounding.setItemMeta(s_meta);

        for (int i = 0; i < 54; i++) {
            FPGUI.setItem(i, Surrounding);
        }

        for (UUID key : UltimateFreeze.FrozenPlayers.keySet()) {
            for (int i = 0; i < UltimateFreeze.FrozenPlayers.keySet().size(); i++){
                    ItemStack playerHead = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) SkullType.PLAYER.ordinal());
                    {
                        ArrayList<String> skull_lore = new ArrayList<>();
                        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
                        Player frozen = Bukkit.getPlayer(key);
                        meta.setOwner(frozen.getName());
                        meta.setDisplayName(StringUtils.translate("&a" + frozen.getName()));
                        double health = frozen.getHealth();
                        String world = frozen.getWorld().getName();
                        skull_lore.add(" ");
                        skull_lore.add(StringUtils.translate("&cHealth: " + health + "/20"));
                        skull_lore.add(StringUtils.translate("&aWorld: " + world));
                        skull_lore.add(" ");

                        if(Bukkit.getPlayer(UltimateFreeze.FrozenPlayers.get(key)) == null){
                            OfflinePlayer staff = Bukkit.getOfflinePlayer(UltimateFreeze.FrozenPlayers.get(key));
                            skull_lore.add(StringUtils.translate("&dFrozen by: " + staff.getName()));
                        } else {
                            Player staff = Bukkit.getPlayer(UltimateFreeze.FrozenPlayers.get(key));
                            skull_lore.add(StringUtils.translate("&dFrozen by: " + staff.getName()));
                        }

                        skull_lore.add(StringUtils.translate("&dFrozen at: " + UltimateFreeze.FreezeTime.get(key)));

                        skull_lore.add(" ");
                        skull_lore.add(StringUtils.translate("&b&lRight-Click to Teleport to " + frozen.getName() + "!"));
                        skull_lore.add(StringUtils.translate("&b&lLeft-Click to Un-Freeze!"));

                        meta.setLore(skull_lore);
                        playerHead.setItemMeta(meta);
                        FPGUI.setItem(i, playerHead);
                    }

            }
        }

        return FPGUI;
    }
}
