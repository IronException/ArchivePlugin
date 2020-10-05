package com.github.ironexception.archiveplugin.plugins;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

/**
 * @author l_amp before 2020-10-04
 */
public class ReadOnlyContainer implements Listener {

    private final ArrayList<Inventory> ignoredInv = new ArrayList<>();

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getPlayer().isSneaking()
                || event.useInteractedBlock() == Event.Result.DENY || event.getClickedBlock() == null) {
            return;
        }

        final Player player = event.getPlayer();
        final Block block = event.getClickedBlock();
        if (block.getState() instanceof Container) {
            player.openInventory(((Container) block.getState()).getInventory());
        }
    }

    @EventHandler
    public void onContainerOpen(final InventoryOpenEvent event) {
        if (ignoredInv.contains(event.getInventory())
                || event.getInventory().getLocation().getBlock().getType() == Material.ENDER_CHEST) {
            return;
        }

        final HumanEntity player = event.getPlayer();
        final Inventory realInventory = event.getInventory();
        final Inventory fakeInventory;
        // this is really stupid i hate bukkit
        if (realInventory.getType() == InventoryType.CHEST) {
            fakeInventory = Bukkit.createInventory(player, realInventory.getSize(), realInventory.getType().getDefaultTitle());
        } else {
            fakeInventory = Bukkit.createInventory(player, realInventory.getType(), realInventory.getType().getDefaultTitle());
        }
        fakeInventory.setContents(realInventory.getContents());


        ignoredInv.add(fakeInventory);
        player.openInventory(fakeInventory);

        if (!ignoredInv.contains(event.getInventory())) {
            event.setCancelled(true);
        }
    }

}