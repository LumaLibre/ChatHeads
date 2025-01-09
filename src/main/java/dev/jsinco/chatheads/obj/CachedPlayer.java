package dev.jsinco.chatheads.obj;

import dev.jsinco.chatheads.ChatHeadUtils;
import dev.jsinco.chatheads.ChatHeads;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@SuppressWarnings("deprecation")
public class CachedPlayer extends CachedPlayerConfig {


    private BaseComponent[] avatar;

    public CachedPlayer(Player player) {
        super(player.getUniqueId());
        Bukkit.getScheduler().runTaskAsynchronously(ChatHeads.getPlugin(), () -> this.avatar = ChatHeadUtils.getHead(player));
    }


    public BaseComponent[] getAvatar() {
        return avatar;
    }

    public void setAvatar(BaseComponent[] avatar) {
        this.avatar = avatar;
    }

    public void updateAvatar() {
        Player player = getPlayer();
        if (player == null) {
            return;
        }
        this.avatar = ChatHeadUtils.getHead(player);
    }

    public boolean isDisabledChatHead() {
        Player player = getPlayer();
        if (player == null) {
            return true;
        }
        if (Configuration.debug) {
            ChatHeads.getPlugin().getLogger().info("isDisabled(): " + isDisabled() + " !player.hasResourcePack():" + !player.hasResourcePack() +
                    " checkBedrockPlayer(player.getUniqueId()): " + checkBedrockPlayer(player.getUniqueId()));
            ChatHeads.getPlugin().getLogger().info(String.valueOf(player.getResourcePackStatus()));
        }
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
