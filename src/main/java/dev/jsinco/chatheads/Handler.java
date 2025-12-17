package dev.jsinco.chatheads;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import dev.jsinco.chatheads.api.ChatHeadsInjectEvent;
import dev.jsinco.chatheads.obj.CachedPlayer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.network.chat.ChatType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Handler implements Listener {

    private static final ConcurrentHashMap<UUID, CachedPlayer> cachedPlayers = new ConcurrentHashMap<>();
    private static final Key DEFAULT_FONT_KEY = Key.key("minecraft", "default");

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

                final String displayName = ((ChatType.Bound) container.getModifier().read(1)).name().getString();
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

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        cachedPlayers.remove(event.getPlayer().getUniqueId());
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
        CachedPlayer sender = getCachedPlayer(chatHeadPlayer);

        ChatHeadsInjectEvent injectEvent = new ChatHeadsInjectEvent(sender.getPlayer(), packetRecipient, sender.getAvatar(), receiver.doNotReverseOrientation());
        injectEvent.setCancelled(receiver.isNoResourcePack() || receiver.isDisabled());
        if (!injectEvent.callEvent()) {
            return null;
        }


        Component space = Component.text(" ")
                .font(DEFAULT_FONT_KEY);


        Component avatar = injectEvent.isReverseOrientation() ?
                injectEvent.getAvatar().append(space) :
                space.append(injectEvent.getAvatar());


        String serialized = JSONComponentSerializer.json().serialize(avatar);

        // add to original msg
        WrappedChatComponent wrappedChatComponent = container.getChatComponents().read(0);
        String combinedJson;
        if (wrappedChatComponent != null) {
            combinedJson = receiver.doNotReverseOrientation() ?
                    "[" + serialized + "," + wrappedChatComponent.getJson() + "]" :
                    "[" + wrappedChatComponent.getJson() + "," + serialized + "]";
        } else {
            ChatHeads.getPlugin().getLogger().warning("WrappedChatComponent is null, couldn't add chat head");
            return null;
            //combinedJson = "[" + serialized + "]";
        }

        WrappedChatComponent combinedComponent = WrappedChatComponent.fromJson(combinedJson);
        container.getChatComponents().write(0, combinedComponent);
        return container;
    }
}
