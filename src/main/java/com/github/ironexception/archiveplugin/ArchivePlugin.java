package com.github.ironexception.archiveplugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.github.ironexception.archiveplugin.plugins.CloneInventory;
import com.github.ironexception.archiveplugin.plugins.ItemFrameGetter;
import com.github.ironexception.archiveplugin.plugins.ShulkerPeek;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;

public final class ArchivePlugin extends JavaPlugin {
    private static ArchivePlugin INSTANCE;


    public static ArchivePlugin getInstance() {
        return INSTANCE;
    }

    public static boolean canPlayerDebug(final HumanEntity player) {
        return player.getName().equals("IronException")
                || player.getName().equals("terbin")
                || player.getName().equals("l_amp")
                || player.isOp()
                || player.hasPermission("stealChest"); // TODO how do permissions work?
    }


    @Override
    public void onEnable() {
        INSTANCE = this;
        final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new ItemFrameGetter(), this);

        getServer().getPluginManager().registerEvents(new ShulkerPeek(protocolManager), this);

        getServer().getPluginManager().registerEvents(new CloneInventory(), this);
        //getServer().getPluginManager().registerEvents(new NoChestSteal(), this);


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static void publicDebugMessage(final String message) {
        INSTANCE.getServer().broadcastMessage(message);
    }

}
