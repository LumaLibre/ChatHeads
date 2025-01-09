package dev.jsinco.chatheads;

import dev.jsinco.chatheads.obj.CachedPlayer;
import dev.jsinco.chatheads.obj.Configuration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChatHeadsCommand implements TabExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("Only players can use this command.");
            return false;
        }

        if (strings.length == 0) {
            commandSender.sendMessage(MiniMessage.miniMessage().deserialize(Configuration.prefix +
                    "Running ChatHeads v" + ChatHeads.getPlugin().getDescription().getVersion() + " By <aqua>Jsinco</aqua> & <aqua>Minso</aqua>"));
            return true;
        }

        final CachedPlayer cachedPlayer = Handler.getCachedPlayer(player);

        switch (strings[0].toLowerCase()) {
            case "toggle":
                if (!player.hasPermission("chatheads.toggle")) {
                    commandSender.sendMessage(MiniMessage.miniMessage().deserialize(Configuration.prefix + "<red>You do not have permission to use this command."));
                    return true;
                }

                cachedPlayer.setDisabled(!cachedPlayer.isDisabled());
                cachedPlayer.saveCachedPlayerConfig();
                commandSender.sendMessage(MiniMessage.miniMessage().deserialize(Configuration.prefix +
                        "ChatHeads are now " + (cachedPlayer.isDisabled() ? "<red>disabled" : "<green>enabled")));
                break;
            case "reverse-orientation":
                if (!player.hasPermission("chatheads.reverse-orientation")) {
                    commandSender.sendMessage(MiniMessage.miniMessage().deserialize(Configuration.prefix + "<red>You do not have permission to use this command."));
                    return true;
                }

                cachedPlayer.setReverseOrientation(!cachedPlayer.isReverseOrientation());
                cachedPlayer.saveCachedPlayerConfig();
                commandSender.sendMessage(MiniMessage.miniMessage().deserialize(Configuration.prefix +
                        "ChatHeads orientation is now " + (cachedPlayer.doNotReverseOrientation() ? "<red>normal" : "<green>reversed")));
                break;
            case "reload":
                if (!player.hasPermission("chatheads.reload")) {
                    commandSender.sendMessage(MiniMessage.miniMessage().deserialize(Configuration.prefix + "<red>You do not have permission to use this command."));
                    return true;
                }


                Configuration.reloadConfig();
                commandSender.sendMessage(MiniMessage.miniMessage().deserialize(Configuration.prefix + "Configuration reloaded."));
                break;
        }


        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of("toggle", "reverse-orientation", "reload");
    }
}
