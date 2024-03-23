package net.minso.chathead;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ToggleChatHeads implements CommandExecutor {

    private final static String prefix = ChatColor.translateAlternateColorCodes('&', "&x&f&4&9&8&f&6&lI&x&c&9&9&0&f&9&ln&x&9&d&8&8&f&c&lf&x&7&2&8&0&f&f&lo &r&8»&x&e&2&e&2&e&2 ");

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(prefix + "Only players can use this command.");
            return true;
        }

        if (useChatHeads(player)) {
            player.getPersistentDataContainer().set(new NamespacedKey(ChatHead.getPlugin(), "disabled"), PersistentDataType.SHORT, (short) 1);
            player.sendMessage(prefix + "Chat heads have been disabled.");
        } else {
            player.getPersistentDataContainer().remove(new NamespacedKey(ChatHead.getPlugin(), "disabled"));
            player.sendMessage(prefix + "Chat heads have been enabled.");
        }

        return true;
    }


    public static boolean useChatHeads(Player player) {
        return !player.getPersistentDataContainer().has(new NamespacedKey(ChatHead.getPlugin(), "disabled"), PersistentDataType.SHORT);
    }
}
