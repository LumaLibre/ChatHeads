package dev.jsinco.chatheads.integration;

import dev.jsinco.chatheads.Handler;
import dev.jsinco.chatheads.obj.CachedPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public final class ChatHeadsAPI {

    public static Component getChatHead(Player player) {
        return Handler.getCachedPlayer(player).getAvatar();
    }

    public static boolean canSeeChatHeads(Player player) {
        return Handler.getCachedPlayer(player).isDisabled();
    }

    public static CachedPlayer getCachedPlayer(Player player) {
        return Handler.getCachedPlayer(player);
    }
}
