package dev.jsinco.chatheads;

import dev.jsinco.abstractjavafilelib.FileLibSettings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChatHeads extends JavaPlugin {

    private static ChatHeads plugin;

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
    }

    public static ChatHeads getPlugin() {
        return plugin;
    }

}
