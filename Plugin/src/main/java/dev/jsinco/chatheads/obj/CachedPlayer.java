package dev.jsinco.chatheads.obj;

import dev.jsinco.chatheads.ChatHeads;
import dev.jsinco.chatheads.ChatHeadUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class CachedPlayer extends CachedPlayerConfig {

    private final Player player;
    private BaseComponent[] avatar;

    public CachedPlayer(Player player) {
        super(player);
        this.player = player;
        Bukkit.getScheduler().runTaskAsynchronously(ChatHeads.getPlugin(), () -> this.avatar = ChatHeadUtils.getChatHead(player.getUniqueId()));
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
        this.avatar = ChatHeadUtils.getChatHead(player.getUniqueId());
    }

    public boolean isDisabledChatHead() {
        return isDisabled() || !player.hasResourcePack();
    }

    public boolean doNotReverseOrientation() {
        return !isReverseOrientation();
    }
}
