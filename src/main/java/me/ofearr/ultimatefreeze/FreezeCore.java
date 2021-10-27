package me.ofearr.ultimatefreeze;

import me.ofearr.ultimatefreeze.Inventories.FreezeInventory;
import me.ofearr.ultimatefreeze.Inventories.LegacyFreezeInventory;
import me.ofearr.ultimatefreeze.Utils.StringUtils;
import me.ofearr.ultimatefreeze.Utils.VersionUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FreezeCore implements Listener {

    private static UltimateFreeze plugin;
    private static String prefix = plugin.getConfig().getString("plugin-prefix");

    public FreezeCore(UltimateFreeze uF){
        this.plugin = uF;
    }

    public static void unFreezePlayer(Player player, Player frozen){
        plugin.FrozenPlayers.remove(frozen.getUniqueId());
        plugin.FreezeTime.remove(frozen.getUniqueId());
        frozen.closeInventory();
        if(plugin.getConfig().getBoolean("alert-staff-on-unfreeze") == true){
            for(Player staff : Bukkit.getOnlinePlayers()){
                if(staff.hasPermission("ultimatefreeze.notify")){
                    String msg = plugin.getConfig().getString("staff-unfreeze-message").replace("{prefix}", prefix).replace("<player>", frozen.getName()).replace("<staff>", player.getName());
                    staff.sendMessage(StringUtils.translate(msg));
                }
            }
        } else {
            String msg = plugin.getConfig().getString("staff-freeze-message").replace("{prefix}", prefix).replace("<player>", frozen.getName()).replace("<staff>", player.getName());
            player.sendMessage(StringUtils.translate(msg));
        }
        frozen.setAllowFlight(false);
        frozen.sendMessage(StringUtils.translate(plugin.getConfig().getString("player-unfreeze-message").replace("{prefix}", prefix).replace("<player>", frozen.getName()).replace("<staff>", player.getName())));
    }

    public static void freezePlayer(Player player, Player frozen){
        plugin.FrozenPlayers.put(frozen.getUniqueId(), player.getUniqueId());

        if(plugin.getConfig().getBoolean("when-frozen.teleport.enabled") == true) {
            try {
                World world = Bukkit.getWorld(plugin.getConfig().getString("when-frozen.teleport.world"));
                double X = plugin.getConfig().getDouble("when-frozen.teleport.X");
                double Y = plugin.getConfig().getDouble("when-frozen.teleport.Y");
                double Z = plugin.getConfig().getDouble("when-frozen.teleport.Z");

                Location teleportLoc = new Location(world, X,Y,Z);

                frozen.teleport(teleportLoc);
            } catch (IllegalArgumentException e) {
                System.out.println("[UltimateFreeze] Invalid world provided for the teleport location for when a player gets frozen!");
            }
        }

        Date current = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

        plugin.FreezeTime.put(frozen.getUniqueId(), dateFormat.format(current));

        frozen.sendMessage(StringUtils.translate(plugin.getConfig().getString("player-freeze-message").replace("{prefix}", prefix).replace("<player>", frozen.getName()).replace("<staff>", player.getName())));
        frozen.setAllowFlight(true);
        if(VersionUtil.isLegacy()){
            frozen.openInventory(new LegacyFreezeInventory(plugin).GUI());
        } else {
            frozen.openInventory(new FreezeInventory(plugin).GUI());
        }
        if(plugin.getConfig().getBoolean("alert-staff-on-freeze") == true){
            for(Player staff : Bukkit.getOnlinePlayers()){
                if(staff.hasPermission("ultimatefreeze.notify")){
                    String msg = plugin.getConfig().getString("staff-freeze-message").replace("{prefix}", prefix).replace("<player>", frozen.getName()).replace("<staff>", player.getName());
                    staff.sendMessage(StringUtils.translate(msg));
                }
            }
        } else {
            String msg = plugin.getConfig().getString("staff-freeze-message").replace("{prefix}", prefix).replace("<player>", frozen.getName()).replace("<staff>", player.getName());
            player.sendMessage(StringUtils.translate(msg));
        }
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void FreezeCoreLogout(PlayerQuitEvent e){
        Player player = e.getPlayer();
        if(UltimateFreeze.FrozenPlayers.containsKey(player.getUniqueId())){
            for(Player staff : Bukkit.getOnlinePlayers()){
                if(staff.hasPermission("ultimatefreeze.notify")){
                    String logoutChatMSG = plugin.getConfig().getString("logout-frozen.staff-chat-msg").replace("{prefix}", prefix).replace("<player>", player.getName());
                    staff.sendMessage(UltimateFreeze.converter(logoutChatMSG));
                }
            }

            UltimateFreeze.FrozenPlayers.remove(player.getUniqueId());
            UltimateFreeze.FreezeTime.remove(player.getUniqueId());
            player.setAllowFlight(false);

            if(plugin.getConfig().getBoolean("logout-frozen.issue-commands")){
                List<String> commands = plugin.getConfig().getStringList("logout-frozen.commands");
                for(int i = 0; i < commands.size(); i++){
                    String cmd = commands.get(i).replace("<player>", player.getName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                }
                System.out.println("[UltimateFreeze] Successfully issued logout commands for " + player.getName() + "!");
            }

        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void FreezeCoreDrop(PlayerDropItemEvent e){
        Player player = e.getPlayer();
        if(UltimateFreeze.FrozenPlayers.containsKey(player.getUniqueId())){
            if(plugin.getConfig().getBoolean("when-frozen.disable-item-drop")){
                e.setCancelled(true);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGH)
    public void FreezeCorePickup(PlayerPickupItemEvent e){
        Player player = e.getPlayer();
        if(UltimateFreeze.FrozenPlayers.containsKey(player.getUniqueId())){
            if(plugin.getConfig().getBoolean("when-frozen.disable-item-pickup")){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void FreezeCoreDamage(EntityDamageEvent e){
        if(!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        if(UltimateFreeze.FrozenPlayers.containsKey(player.getUniqueId())){
            if(plugin.getConfig().getBoolean("when-frozen.disable-damage")){
                e.setCancelled(true);
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void FreezeCoreDamager(EntityDamageByEntityEvent e){
        if(!(e.getDamager() instanceof Player)) return;
        Player player = (Player) e.getDamager();
        if(UltimateFreeze.FrozenPlayers.containsKey(player.getUniqueId())){
            if(plugin.getConfig().getBoolean("when-frozen.disable-damage")){
                e.setCancelled(true);
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void FreezeCoreMove(PlayerMoveEvent e){
        Player player = e.getPlayer();
        if(UltimateFreeze.FrozenPlayers.containsKey(player.getUniqueId())){
            double X = e.getFrom().getX();
            double Z = e.getFrom().getZ();

            if(plugin.getConfig().getBoolean("when-frozen.disable-falling") == false && e.getTo().getX() == X && e.getTo().getZ() == Z){
                return;
            }
            else if(plugin.getConfig().getBoolean("when-frozen.disable-looking") == false && e.getTo() == e.getFrom()){
                return;
            }

            else if(plugin.getConfig().getBoolean("when-frozen.disable-movement")){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void FreezeCoreChat(AsyncPlayerChatEvent e){
        Player player = e.getPlayer();
        if(UltimateFreeze.FrozenPlayers.containsKey(player.getUniqueId())){
            if(plugin.getConfig().getBoolean("when-frozen.disable-chat")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void FreezeCorePlace(BlockPlaceEvent e){
        Player player = e.getPlayer();
        if(UltimateFreeze.FrozenPlayers.containsKey(player.getUniqueId())){
            if(plugin.getConfig().getBoolean("when-frozen.disable-block-place")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void FreezeCoreBreak(BlockBreakEvent e){
        Player player = e.getPlayer();
        if(UltimateFreeze.FrozenPlayers.containsKey(player.getUniqueId())){
            if(plugin.getConfig().getBoolean("when-frozen.disable-block-break")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void FreezeCoreUse(PlayerInteractEvent e){
        Player player = e.getPlayer();
        if(UltimateFreeze.FrozenPlayers.containsKey(player.getUniqueId())){
            if(plugin.getConfig().getBoolean("when-frozen.disable-interaction")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void FreezeCoreCMD(PlayerCommandPreprocessEvent e){
        Player player = e.getPlayer();
        if(UltimateFreeze.FrozenPlayers.containsKey(player.getUniqueId())){
            if(plugin.getConfig().getBoolean("when-frozen.disable-chat")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void preventCloseInv(InventoryCloseEvent e){
        if(e.getInventory() == null) return;
        if(e.getView() == null) return;
        if(e.getPlayer() == null) return;
        Player player = (Player) e.getPlayer();
        if(UltimateFreeze.FrozenPlayers.containsKey(e.getPlayer().getUniqueId())){
            if(plugin.getConfig().getBoolean("when-frozen.disable-close-inventory")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (VersionUtil.isLegacy()) {
                            player.openInventory(new LegacyFreezeInventory(plugin).GUI());
                        } else {
                            player.openInventory(new FreezeInventory(plugin).GUI());
                        }
                    }
                }.runTaskLater(this.plugin, 1L);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void InvClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        if (e.getClickedInventory() == null) {
            return;
        }
        if (e.getView().getTitle() == null) {
            return;
        }

        if (e.getSlotType() == InventoryType.SlotType.OUTSIDE) return;
        if (item == null) return;
        if(e.getCursor() == null) return;

        if (e.getView().getTitle().equalsIgnoreCase(UltimateFreeze.converter("&c&lFrozen Players"))) {

            if(!(item.getType().toString().contains("STAINED_GLASS_PANE"))){
                String utg = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                Player frozen = Bukkit.getServer().getPlayer(utg);
                if(frozen == null){
                    return;
                }
                ClickType click = e.getClick();
                if(click == null) return;

                if(click == ClickType.LEFT){
                    unFreezePlayer(player, frozen);
                    player.closeInventory();
                } else if(click == ClickType.RIGHT){
                    player.teleport(frozen.getLocation());
                }


            }

            e.setCancelled(true);

        }

        if(e.getView().getTitle().equalsIgnoreCase(StringUtils.translate(plugin.getConfig().getString("gui-name")))){
            e.setCancelled(true);
        }

    }
}
