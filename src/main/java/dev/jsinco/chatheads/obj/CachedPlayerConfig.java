package dev.jsinco.chatheads.obj;

import dev.jsinco.chatheads.ChatHeads;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CachedPlayerConfig {

    protected final UUID playerUUID;
    private boolean disabled = false;
    private boolean reverseOrientation = false;

    public CachedPlayerConfig(UUID playerUUID) {
        this.playerUUID = playerUUID;
        loadCachedPlayerConfig();
    }

    public static CachedPlayerConfig loadCachedPlayer(UUID playerUUID) {
        return new CachedPlayerConfig(playerUUID);
    }

    @Nullable
    public Player getPlayer() {
        if (Configuration.debug) {
            ChatHeads.getPlugin().getLogger().info("Found Bukkit Player " + playerUUID);
        }
        return Bukkit.getPlayer(playerUUID);
    }


    public void loadCachedPlayerConfig() {
        Player player = getPlayer();
        if (player == null) {
            return;
        }
        String data = player.getPersistentDataContainer().get(new NamespacedKey(ChatHeads.getPlugin(), "chatheads"), PersistentDataType.STRING);

        if (data == null) {
            return;
        }

        String[] split = data.split(";");
        for (String string : split) {
            String[] split2 = string.split("=");
            if (split2.length != 2) {
                continue;
            }

            switch (split2[0]) {
                case "disabled":
                    disabled = Boolean.parseBoolean(split2[1]);
                    break;
                case "reverseOrientation":
                    reverseOrientation = Boolean.parseBoolean(split2[1]);
                    break;
            }
        }
    }

    public void saveCachedPlayerConfig() {
        Player player = getPlayer();
        if (player == null) {
            return;
        }
        if (!disabled && !reverseOrientation) {
            player.getPersistentDataContainer().remove(new NamespacedKey(ChatHeads.getPlugin(), "chatheads"));
        } else {
            String string = "disabled=" + disabled + ";reverseOrientation=" + reverseOrientation;
            player.getPersistentDataContainer().set(new NamespacedKey(ChatHeads.getPlugin(), "chatheads"), PersistentDataType.STRING, string);
        }
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isReverseOrientation() {
        return reverseOrientation;
    }

    public void setReverseOrientation(boolean reverseOrientation) {
        this.reverseOrientation = reverseOrientation;
    }

}
