package com.github.ironexception.archiveplugin.plugins;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static com.github.ironexception.archiveplugin.ArchivePlugin.canPlayerDebug;


/**
 * @author IronException on 2020-10-01
 */
public class NoChestSteal implements Listener {


    private boolean active = true;

    @EventHandler
    public void onPlayerInteract(final InventoryClickEvent event) {
        if (canPlayerDebug(event.getWhoClicked()) && !active) {
            return;
        }

        if (event.getAction() == InventoryAction.NOTHING) {
            return;
        }
        if (event.getClickedInventory() == null) {
            return; // TODO actually Im not sure whether this is safe. but kinda needs to happen because it is null when you drop items
        }

        // TODO WARNING the type has to be of the inventory (not the clicked inventory) otherwise it will show as the player and will always get accepted then
        if (shallCancel(event.getInventory().getType(), event.getClickedInventory(), event.getAction())) {

            event.setCancelled(true);

            final ItemStack currentItem = event.getCurrentItem();
            if (shouldGiveItem(currentItem, event.getCursor(), event.getClickedInventory())) {


                event.getWhoClicked().setItemOnCursor(currentItem);
            }
        }
    }

    /**
     * @param type      the inventory type
     * @param inventory actual inventory
     * @param action    the action how the items are handled
     * @return whether the action should be cancelled
     * @WARNING the type has to be of the overall inventory!
     */
    private boolean shallCancel(final InventoryType type, final Inventory inventory, final InventoryAction action) {
        if (isInventoryOkay(type)) {
            return false;
        }

        if (!(inventory.getHolder() instanceof HumanEntity)) {
            return true; // not accepted
        }


        return !isActionSafe(action);
    }

    private boolean isInventoryOkay(final InventoryType inventoryType) {
        return inventoryType == InventoryType.CRAFTING
                || inventoryType == InventoryType.CREATIVE
                || inventoryType == InventoryType.PLAYER
                || inventoryType == InventoryType.WORKBENCH // drops
                || inventoryType == InventoryType.ANVIL // drops
                || inventoryType == InventoryType.BEACON // drops
                || inventoryType == InventoryType.ENCHANTING // drops
                || inventoryType == InventoryType.ENDER_CHEST;
    }

    private boolean isActionSafe(final InventoryAction action) {
        switch (action) {
            case NOTHING: // completely fine. you do nothing
            case PICKUP_ALL: // its 1 stack. fine in player inventory
            case PICKUP_SOME: // its 1 stack. fine in player inventory
            case PICKUP_HALF: // its 1 stack. fine in player inventory
            case PICKUP_ONE: // its 1 stack. fine in player inventory
            case PLACE_ALL: // its 1 stack. fine in player inventory
            case PLACE_SOME: // its 1 stack. fine in player inventory
            case PLACE_ONE: // its 1 stack. fine in player inventory
            case HOTBAR_SWAP: // from player inventory its ok (hotbar is also player...)

            case SWAP_WITH_CURSOR: // middleclick?
            case CLONE_STACK: // I have no idea
                return true;
            case DROP_ALL_CURSOR: // do anything here?
            case DROP_ONE_CURSOR: // do anything here?
            case DROP_ALL_SLOT: // do anything here?
            case DROP_ONE_SLOT: // do anything here?
            case HOTBAR_MOVE_AND_READD: // wht does this do?

            case COLLECT_TO_CURSOR: // bad if items in chest
            case MOVE_TO_OTHER_INVENTORY: // makes sense to cancel this
            case UNKNOWN: // better save then sorry
                return false;
        }
        return false;
    }

    private boolean shouldGiveItem(final ItemStack item, final ItemStack cursor, final Inventory inventory) {
        // TODO these values arent actually like how they are expected (cursor is empty here when the item in the slot and in cursor was the same)
        if (item == null) {
            return false;
        }
        if (item.getType() == Material.AIR) {
            return false;
        }
        if (cursor == null) {
            return false;
        }
        if (cursor.getType() != Material.AIR) {
            return false;
        }

        return !(inventory.getHolder() instanceof HumanEntity);
    }

    @EventHandler
    public void onPlayerDragEvent(final InventoryDragEvent event) {
        if (canPlayerDebug(event.getWhoClicked()) && !active) {
            return;
        }

        if (isInventoryOkay(event.getInventory().getType())) {
            return;
        }

        if (event.getInventory().getHolder() instanceof HumanEntity) {
            return; // accepted
        }
        if (event.getRawSlots().stream().allMatch(slot -> slot >= event.getView().getTopInventory().getSize())) {
            return; // should work. because there is the player inventory
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        if (canPlayerDebug(event.getPlayer())) {
            if (event.getMessage().equals("steal plugin")) {
                active = !active;
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.GREEN + "toggled the plugin for players with debug permission to " + active);
                if (active) {
                    event.getPlayer().sendMessage(ChatColor.GREEN + "you can no longer change any items in chests");
                } else {
                    event.getPlayer().sendMessage(ChatColor.GREEN + "you can change items in chests for debugging");
                }
            }
        }
    }

}
