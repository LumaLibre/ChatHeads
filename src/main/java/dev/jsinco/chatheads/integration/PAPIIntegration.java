package dev.jsinco.chatheads.integration;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PAPIIntegration extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "chatheads";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Jsinco";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.6";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        Player onlinePlayer;

        if (player != null && player.isOnline() && params.equals("self")) {
            onlinePlayer = player.getPlayer();
        } else {
            onlinePlayer = Bukkit.getPlayer(params);
        }

        if (onlinePlayer == null) {
            return null;
        }
        return LegacyComponentSerializer.legacyAmpersand().serialize(ChatHeadsAPI.getChatHead(onlinePlayer));
    }
}
