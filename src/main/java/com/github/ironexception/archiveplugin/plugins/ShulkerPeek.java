package com.github.ironexception.archiveplugin.plugins;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.github.ironexception.archiveplugin.ArchivePlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.Optional;

/**
 * @author IronException on 2020-10-02
 */
public class ShulkerPeek extends PacketAdapter implements Listener {

    // TODO this plugin assumes that NoChestSteal is also on. So if this gets disabled there have to be extra checks in here to not change any items...
    // TODO currently this also assumes you cant place shulkers in the world. because it also opens the inventory then. // TODO cancel the event if it is opened

    public ShulkerPeek(final ProtocolManager manager) {
        super(ArchivePlugin.getInstance(), ListenerPriority.NORMAL,
                PacketType.Play.Client.BLOCK_PLACE, PacketType.Play.Client.USE_ITEM);
        manager.addPacketListener(this);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.isCancelled()) {
            return;
        }
        // am I dumb or are these types swapped?
        if (event.getPacketType() == PacketType.Play.Client.BLOCK_PLACE
                || event.getPacketType() == PacketType.Play.Client.USE_ITEM) {
            final PacketContainer packet = event.getPacket();
            final EnumWrappers.Hand hand = packet.getHands().read(0);

            final Player player = event.getPlayer();
            switch (hand) {
                case OFF_HAND:
                    checkItem(player, player.getInventory().getItemInOffHand());
                    break;
                case MAIN_HAND:
                    checkItem(player, player.getInventory().getItemInMainHand());
                    break;
            }
            // TODO maybe cancel the event if it worked...


        }


    }

    private void checkItem(final Player player, final ItemStack item) {
        if (isShulker(item)) {
            tryOpenShulker(player, item);
        }
    }

    @EventHandler
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        if (event.getMessage().matches(".?peek")) {
            event.setCancelled(true);

            final Player player = event.getPlayer();
            final Optional<ItemStack> item = getShulker(player);
            if (item.isPresent()) {
                tryOpenShulker(player, item.get());
            } else {
                player.sendMessage(ChatColor.AQUA + "put a shulker box in your inventory to peek in it");
            }


        }

    }

    private void tryOpenShulker(final Player player, final ItemStack item) {
        // TODO maybe check whether an inventory is open and then cancel the open
        if (item.getItemMeta() instanceof BlockStateMeta) {
            final BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
            if (meta.getBlockState() instanceof ShulkerBox) {
                // TODO the display name is not correct actually (when it is not set or something vanilla says "Shulker box" but we also give the color)
                // TODO maybe a better custom holder...
                final Inventory inventory = Bukkit.createInventory(() -> ((ShulkerBox) meta.getBlockState()).getInventory(), InventoryType.SHULKER_BOX, item.getI18NDisplayName());
                inventory.setContents(((ShulkerBox) meta.getBlockState()).getInventory().getContents());
                player.openInventory(inventory);

            }
        }
    }

    private Optional<ItemStack> getShulker(final Player player) {
        final PlayerInventory inventory = player.getInventory();
        if (isShulker(inventory.getItemInMainHand())) {
            return Optional.of(inventory.getItemInMainHand());
        }
        if (isShulker(inventory.getItemInOffHand())) {
            return Optional.of(inventory.getItemInOffHand());
        }

        // search whether there is a shulker somewhere else in the inventory
        final InventoryView openInventory = player.getOpenInventory();
        final int max = openInventory.countSlots();
        for (int i = 0; i < max; i++) {
            final ItemStack item = openInventory.getItem(i);
            if (isShulker(item)) {
                return Optional.of(item);
            }

        }

        return Optional.empty();
    }

    private boolean isShulker(final ItemStack item) {
        return isShulker(item.getType());
    }

    private boolean isShulker(final Material material) {
        return material == Material.WHITE_SHULKER_BOX
                || material == Material.ORANGE_SHULKER_BOX
                || material == Material.MAGENTA_SHULKER_BOX
                || material == Material.LIGHT_BLUE_SHULKER_BOX
                || material == Material.YELLOW_SHULKER_BOX
                || material == Material.LIME_SHULKER_BOX
                || material == Material.PINK_SHULKER_BOX
                || material == Material.GRAY_SHULKER_BOX
                || material == Material.SILVER_SHULKER_BOX
                || material == Material.CYAN_SHULKER_BOX
                || material == Material.PURPLE_SHULKER_BOX
                || material == Material.BLUE_SHULKER_BOX
                || material == Material.BROWN_SHULKER_BOX
                || material == Material.GREEN_SHULKER_BOX
                || material == Material.RED_SHULKER_BOX
                || material == Material.BLACK_SHULKER_BOX;
    }


}