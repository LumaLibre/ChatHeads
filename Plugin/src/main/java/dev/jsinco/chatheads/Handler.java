package dev.jsinco.chatheads;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import dev.jsinco.chatheads.obj.CachedPlayer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.network.chat.ChatType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class Handler extends BukkitRunnable implements Listener {

    private static final LinkedHashMap<UUID, CachedPlayer> cachedPlayers = new LinkedHashMap<>();

    private final ChatHeads plugin;

    public Handler(ChatHeads plugin) {
        this.plugin = plugin;
    }

    public void registerChatPacketListener() {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Server.CHAT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Player chatHeadPlayer = Bukkit.getPlayer(packet.getUUIDs().getValues().get(0));
                if (chatHeadPlayer == null) {
                    return;
                }

                PacketContainer newPacket = chatHeadPacket(packet, event.getPlayer(), chatHeadPlayer);
                if (newPacket != null) {
                    event.setPacket(newPacket);
                }
            }
        });

        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Server.DISGUISED_CHAT) {
            @Override
            public void onPacketSending(PacketEvent event) {

                final PacketContainer container = event.getPacket();



                // get name
                Player player = null;

                final String displayName = ((ChatType.BoundNetwork) container.getModifier().read(1)).name().getString();
                for (Player aPlayer : Bukkit.getOnlinePlayers()) {
                    final String playerDisplayName = PlainTextComponentSerializer.plainText().serialize(aPlayer.displayName());
                    if (displayName.equals(playerDisplayName)) {
                        player = aPlayer;
                        break;
                    }
                }

                if (player == null) {
                    return;
                }


                PacketContainer newPacket = chatHeadPacket(container, event.getPlayer(), player);
                if (newPacket != null) {
                    event.setPacket(newPacket);
                }
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (cachedPlayers.containsKey(player.getUniqueId())) {
            return;
        }
        CachedPlayer cachedPlayer = new CachedPlayer(player);
        cachedPlayers.put(player.getUniqueId(), cachedPlayer);
    }

    @Override
    public void run() {
        final List<UUID> playersToRemove = new ArrayList<>();

        for (final CachedPlayer cachedPlayer : cachedPlayers.values()) {
            final Player player = cachedPlayer.getPlayer();
            if (!player.isOnline()) {
                playersToRemove.add(player.getUniqueId());
            }
        }

        for (final UUID uuid : playersToRemove) {
            cachedPlayers.remove(uuid);
        }
    }

    public void addCachedPlayer(Player player) {
        cachedPlayers.put(player.getUniqueId(), new CachedPlayer(player));
    }

    public static CachedPlayer getCachedPlayer(Player player) {
        CachedPlayer cachedPlayer = cachedPlayers.get(player.getUniqueId());
        if (cachedPlayer == null) {
            cachedPlayer = new CachedPlayer(player);
            cachedPlayers.put(player.getUniqueId(), cachedPlayer);
        }
        return cachedPlayer;
    }

    @Nullable
    public PacketContainer chatHeadPacket(final PacketContainer container, final Player packetRecipient, final Player chatHeadPlayer) {
        CachedPlayer receiver = getCachedPlayer(packetRecipient);
        if (receiver.isDisabledChatHead()) {
            return null;
        }

        // need to get the sender of the msg
        CachedPlayer sender = getCachedPlayer(chatHeadPlayer);
        if (sender.getAvatar() == null) {
            return null;
        }

        final TextComponent textComponent = new TextComponent(" ");
        textComponent.setFont("minecraft:default");

        final BaseComponent[] avatar = receiver.doNotReverseOrientation() ?
                new ComponentBuilder().append(sender.getAvatar()).append(textComponent).create() :
                new ComponentBuilder().append(textComponent).append(sender.getAvatar()).create();

        // add to original msg
        WrappedChatComponent wrappedChatComponent = container.getChatComponents().read(0);
        String combinedJson = receiver.doNotReverseOrientation() ?
                "[" + ComponentSerializer.toString(avatar) + "," + wrappedChatComponent.getJson() + "]" :
                "[" + wrappedChatComponent.getJson() + "," + ComponentSerializer.toString(avatar) + "]";

        WrappedChatComponent combinedComponent = WrappedChatComponent.fromJson(combinedJson);
        container.getChatComponents().write(0, combinedComponent);
        return container;
    }
}
