package com.miyuki.autoforge;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Listeners implements Listener {
    private static Map<UUID,BukkitTask>   tasks = new ConcurrentHashMap<>();

    @EventHandler
    public void onListening(PlayerJoinEvent event){
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(AutoForge.plugin, new Runnable() {
            @Override
            public void run() {
                Player player = event.getPlayer();
                if(!player.isOnline()){
                    return;
                }
                Chunk chunk = player.getLocation().getChunk();
                for(BlockState i :chunk.getTileEntities()){
                    if(i instanceof Furnace){
                        Furnace furnace = (Furnace) i;
                        Block block = i.getBlock().getRelative(BlockFace.DOWN);
                        if(block.getType() == Material.AIR){
                            continue;
                        }
                        if(block.getType() == Material.HOPPER){
                            Hopper hopper = (Hopper) block.getState();


                        

                            if(furnace.getInventory().getSmelting()!=null) {
                                if (furnace.getCookTime()==0) {
                                    if (hasSpace(furnace.getInventory().getSmelting(), hopper.getInventory(), furnace.getInventory().getSmelting().getAmount())) {
                                        addItems(hopper.getInventory(), furnace.getInventory().getSmelting(), furnace.getInventory().getSmelting().getAmount());
                                        furnace.getInventory().remove(furnace.getInventory().getSmelting());
                                        player.updateInventory();
                                    }
                                }
                            }
                            if(furnace.getInventory().getFuel()!=null) {
                                if (!furnace.getInventory().getFuel().getType().isFuel()) {
                                    if (hasSpace(furnace.getInventory().getFuel(), hopper.getInventory(), furnace.getInventory().getFuel().getAmount())) {
                                        addItems(hopper.getInventory(), furnace.getInventory().getFuel(), furnace.getInventory().getFuel().getAmount());
                                        furnace.getInventory().remove(furnace.getInventory().getFuel());
                                        player.updateInventory();
                                    }
                                }
                            }
                        }

                    }
                }
            }
        },0L,40L);
        if(tasks.get(event.getPlayer().getUniqueId())!=null){
            return;
        }
        tasks.put(event.getPlayer().getUniqueId(),task);
    }

    @EventHandler
    public void onListening(PlayerQuitEvent event){
        if(tasks.get(event.getPlayer().getUniqueId())!=null){
            tasks.get(event.getPlayer().getUniqueId()).cancel();
        }
    }


    /**
     * Add item to inventory
     * @param inventory inventory
     * @param itemStack item
     * @param amount amount
     */
    public static void addItems(Inventory inventory, ItemStack itemStack, int amount) {
        itemStack.setAmount(1);
        for (int i = 0; i < amount; i++) {
            inventory.addItem(new ItemStack[] { itemStack });
        }
    }


    /**
     * Check if inventory has space for item
     * @param is item
     * @param inventory inventory
     * @param amount amount
     * @return true if has space, else flase
     */
    public static boolean hasSpace(ItemStack is, Inventory inventory, int amount) {
        int slots = (int)Math.ceil((amount / is.getMaxStackSize()));
        if (amount % is.getMaxStackSize() != 0)
            slots++;
        if (slots == 0)
            slots = 1;
        return (getEmptySlots(inventory) >= slots);
    }

    /**
     * Get empty slots in inventory
     * @param inventory inventory
     * @return empty slots
     */
    public static int getEmptySlots(Inventory inventory) {
        int amount = 0;
        for (ItemStack is : inventory.getStorageContents()) {
            if (is == null || is.getType() == Material.AIR)
                amount++;
        }
        return amount;
    }
}
