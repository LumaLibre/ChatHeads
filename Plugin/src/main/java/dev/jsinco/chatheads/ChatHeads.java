package dev.jsinco.chatheads;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import dev.jsinco.abstractjavafilelib.FileLibSettings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChatHeads extends JavaPlugin {

    private static ChatHeads plugin;
    public static boolean floodgateEnabled = false;

    @Override
    public void onEnable() {
        plugin = this;
        FileLibSettings.set(getDataFolder(), getLogger());
        final Handler handler = new Handler(this);

        getServer().getPluginManager().registerEvents(handler, this);
        handler.registerChatPacketListener();
        handler.runTaskTimerAsynchronously(this, 0, 6000);

        for (final Player player : Bukkit.getOnlinePlayers()) {
            handler.addCachedPlayer(player);
        }
        getCommand("chatheads").setExecutor(new ChatHeadsCommand());

        if (Bukkit.getPluginManager().getPlugin("floodgate") != null) {
            floodgateEnabled = true;
        }
    }

    @Override
    public void onDisable() {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.removePacketListeners(this);
    }

    public static ChatHeads getPlugin() {
        return plugin;
    }

}
