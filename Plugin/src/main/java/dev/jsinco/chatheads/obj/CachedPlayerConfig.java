package dev.jsinco.chatheads.obj;

import dev.jsinco.chatheads.ChatHeads;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class CachedPlayerConfig {

    private final Player player;
    private boolean disabled = false;
    private boolean reverseOrientation = false;

    public CachedPlayerConfig(Player player) {
        this.player = player;
        loadCachedPlayerConfig();
    }

    public static CachedPlayerConfig loadCachedPlayer(Player player) {
        return new CachedPlayerConfig(player);
    }

    public void loadCachedPlayerConfig() {
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
        if (!disabled && !reverseOrientation) {
            player.getPersistentDataContainer().remove(new NamespacedKey(ChatHeads.getPlugin(), "chatheads"));
        } else {
            String string =  "disabled=" + disabled + ";reverseOrientation=" + reverseOrientation;
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
