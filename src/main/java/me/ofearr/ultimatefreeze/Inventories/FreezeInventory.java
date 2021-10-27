package me.ofearr.ultimatefreeze.Inventories;

import me.ofearr.ultimatefreeze.UltimateFreeze;
import me.ofearr.ultimatefreeze.Utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class FreezeInventory {

    private UltimateFreeze plugin;

    public FreezeInventory(UltimateFreeze uF){
        this.plugin = uF;
    }

    public Inventory GUI(){
        String title = plugin.getConfig().getString("gui-name");
        String SurroundingFC = plugin.getConfig().getString("gui-surrounding").toUpperCase();
        String GUIItem = plugin.getConfig().getString("gui-info-item").toUpperCase();
        String GUIItemName = plugin.getConfig().getString("gui-info-name");
        int GUISize = plugin.getConfig().getInt("gui-size");
        int GUIItemSlot = plugin.getConfig().getInt("gui-info-item-slot");

        Inventory FreezeInventory = Bukkit.createInventory(null, GUISize, StringUtils.translate(title));

        ItemStack Surrounding = null;
        ItemStack Item = null;

        try {
            Surrounding = new ItemStack(Material.getMaterial(SurroundingFC), 1);
            Item = new ItemStack(Material.getMaterial(GUIItem), 1);
        }catch (IllegalArgumentException e){
            System.out.println("[UltimateFreeze] Invalid material(s) provided! Please check the config and confirm that the materials are valid.");
        }

        ItemMeta s_meta = Surrounding.getItemMeta();
        ArrayList<String> s_lore = new ArrayList<>();
        s_lore.add(ChatColor.COLOR_CHAR + "!");
        s_meta.setLore(s_lore);

        ItemMeta i_meta = Item.getItemMeta();
        ArrayList<String> i_lore = new ArrayList<>();
        i_meta.setDisplayName(StringUtils.translate(GUIItemName));
        for (int i = 0; i < plugin.getConfig().getStringList("gui-info-lore").size(); i++) {
            i_lore.add(StringUtils.translate(plugin.getConfig().getStringList("gui-info-lore").get(i)));
        }

        i_meta.setLore(i_lore);

        Surrounding.setItemMeta(s_meta);
        Item.setItemMeta(i_meta);

        for (int i = 0; i < GUISize; i++) {
            FreezeInventory.setItem(i, Surrounding);
        }

        FreezeInventory.setItem(GUIItemSlot, Item);

        return FreezeInventory;
    }
}
