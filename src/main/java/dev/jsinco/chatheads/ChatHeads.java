package dev.jsinco.chatheads;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import dev.jsinco.abstractjavafilelib.FileLibSettings;
import dev.jsinco.chatheads.integration.PAPIIntegration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChatHeads extends JavaPlugin {

    private static ChatHeads plugin;
    private static PAPIIntegration papiIntegration;
    public static boolean floodgateEnabled = false;

    @Override
    public void onEnable() {
        plugin = this;
        FileLibSettings.set(getDataFolder(), getLogger());
        final Handler handler = new Handler(this);

        getServer().getPluginManager().registerEvents(handler, this);
        handler.registerChatPacketListener();

        for (final Player player : Bukkit.getOnlinePlayers()) {
            handler.addCachedPlayer(player);
        }
        getCommand("chatheads").setExecutor(new ChatHeadsCommand());

        if (getServer().getPluginManager().isPluginEnabled("floodgate")) {
            floodgateEnabled = true;
        }

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            papiIntegration = new PAPIIntegration();
            papiIntegration.register();
        }
    }

    @Override
    public void onDisable() {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.removePacketListeners(this);
        if (papiIntegration != null) {
            papiIntegration.unregister();
        }
    }

    public static ChatHeads getPlugin() {
        return plugin;
    }

}
