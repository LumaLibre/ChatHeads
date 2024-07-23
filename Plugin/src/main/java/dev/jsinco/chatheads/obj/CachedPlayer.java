package dev.jsinco.chatheads.obj;

import dev.jsinco.chatheads.ChatHeadUtils;
import dev.jsinco.chatheads.ChatHeads;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.UUID;

@SuppressWarnings("deprecation")
public class CachedPlayer extends CachedPlayerConfig {

    private final Player player;
    private BaseComponent[] avatar;

    public CachedPlayer(Player player) {
        super(player);
        this.player = player;
        Bukkit.getScheduler().runTaskAsynchronously(ChatHeads.getPlugin(), () -> this.avatar = ChatHeadUtils.getHead(player));
    }

    public Player getPlayer() {
        return player;
    }

    public BaseComponent[] getAvatar() {
        return avatar;
    }

    public void setAvatar(BaseComponent[] avatar) {
        this.avatar = avatar;
    }

    public void updateAvatar() {
        this.avatar = ChatHeadUtils.getHead(player);
    }

    public boolean isDisabledChatHead() {
        return isDisabled() || !player.hasResourcePack() || checkBedrockPlayer(player.getUniqueId());
    }

    public boolean doNotReverseOrientation() {
        return !isReverseOrientation();
    }

    public static boolean checkBedrockPlayer(UUID uuid) {
        if (!ChatHeads.floodgateEnabled) {
            return false;
        }

        FloodgateApi floodgateApi = FloodgateApi.getInstance();
        return floodgateApi.isFloodgatePlayer(uuid);
    }
}
