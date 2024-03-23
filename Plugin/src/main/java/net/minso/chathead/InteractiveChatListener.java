package net.minso.chathead;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.loohp.interactivechat.api.events.PreChatPacketSendEvent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.Field;
import java.util.UUID;

public class InteractiveChatListener implements Listener {

    @EventHandler
    public void onICPrePacketSend(PreChatPacketSendEvent event) {
        System.out.println("test");
        if (!ToggleChatHeads.useChatHeads(event.getReciver())) {
            return;
        }

        PacketContainer container = event.getPacket();

        // need to get the sender of the msg
        System.out.println(event.getSender());

        // thanks interactive chat, the sender is always null, so we just have to make a guess by checking all the text components and see if we can find a player
        Player sender = null;
        String msgAsString = ChatColor.stripColor(PlainTextComponentSerializer.plainText().serialize(event.getComponent()));

        String[] msgSplit = msgAsString.split(" ");
        OUTER_LOOP: for (String msg : msgSplit) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (msg.contains(player.getName())) {
                    sender = player;
                    break OUTER_LOOP;
                }
            }
        }
        if (sender == null) {
            return;
        }

        System.out.println("test2");

        // add to original msg
        TextComponent textComponent = new TextComponent();
        textComponent.setText(" ");
        textComponent.setFont("minecraft:default");
        BaseComponent[] head = new ComponentBuilder().append(ChatHead.getHead(sender)).append(textComponent).create();


        WrappedChatComponent wrappedChatComponent = container.getChatComponents().read(0);

        String combinedJson = "[" + ComponentSerializer.toString(head) + "," + wrappedChatComponent.getJson() + "]";

        WrappedChatComponent combinedComponent = WrappedChatComponent.fromJson(combinedJson);

        container.getChatComponents().write(0, combinedComponent);


        try {
            Field field = event.getClass().getDeclaredField("packet");
            field.setAccessible(true);
            field.set(event, container);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        //event.setPacket(container);
    }
}
