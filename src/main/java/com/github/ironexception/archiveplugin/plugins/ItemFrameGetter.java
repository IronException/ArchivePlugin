package com.github.ironexception.archiveplugin.plugins;

import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author l_amp on 2020-09-30
 */
public class ItemFrameGetter implements Listener {

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame)) {
            return;
        }
        final Player player = event.getPlayer();
        final ItemStack item = ((ItemFrame) event.getRightClicked()).getItem();

        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            player.getInventory().setItemInMainHand(item);
            event.setCancelled(true);
        }
    }

}