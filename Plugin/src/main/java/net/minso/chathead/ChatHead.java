package net.minso.chathead;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import dev.jsinco.textureapi.TextureAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

public final class ChatHead extends JavaPlugin {

    private static ChatHead plugin;
    private static boolean useTextureAPI = true;

    @Override
    public void onEnable() {
        plugin = this;
        useTextureAPI = getServer().getPluginManager().getPlugin("TextureAPI") != null;
        getCommand("chatheads").setExecutor(new ToggleChatHeads());
        //getServer().getPluginManager().registerEvents(new InteractiveChatListener(), this);

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();


        protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Server.CHAT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (!ToggleChatHeads.useChatHeads(event.getPlayer()) || !event.getPlayer().hasResourcePack()) {
                    return;
                }

                PacketContainer container = event.getPacket();

                // need to get the sender of the msg
                Player player = Bukkit.getPlayer(container.getUUIDs().getValues().get(0));
                if (player == null) {
                    return;
                }

                // add to original msg
                TextComponent textComponent = new TextComponent();
                textComponent.setText(" ");
                textComponent.setFont("minecraft:default");
                BaseComponent[] head = new ComponentBuilder().append(getHead(player)).append(textComponent).create();


                WrappedChatComponent wrappedChatComponent = container.getChatComponents().read(0);

                String combinedJson = "[" + ComponentSerializer.toString(head) + "," + wrappedChatComponent.getJson() + "]";

                WrappedChatComponent combinedComponent = WrappedChatComponent.fromJson(combinedJson);

                container.getChatComponents().write(0, combinedComponent);
                event.setPacket(container);
            }
        });
    }


    public static BaseComponent[] getHead(Player player) {
        return getHead(player.getUniqueId());
    }

    public static BaseComponent[] getHead(UUID uuid) {
        String[] hexColors = getPixelColorsMinotarNet(uuid.toString());

        if (hexColors == null || hexColors.length < 64) {
            throw new IllegalArgumentException("Hex colors array must have at least 64 elements.");
        }

        TextComponent[][] components = new TextComponent[8][8];

        for (int i = 0; i < 64; i++) {
            int row = i / 8;
            int col = i % 8;
            char unicodeChar = (char) ('\uF000' + (i % 8) + 1);
            char spaceChar;
            TextComponent component = new TextComponent();
            if (i == 7 || i == 15 || i == 23 || i == 31 || i == 39 || i == 47 || i == 55) {
                component.setText(Character.toString(unicodeChar) + Character.toString('\uF101'));
            } else if (i == 63) {
                component.setText(Character.toString(unicodeChar));
            } else {
                component.setText(Character.toString(unicodeChar) + Character.toString('\uF102'));
            }

            component.setColor(ChatColor.of(hexColors[i]));
            component.setFont("minecraft:playerhead");
            components[row][col] = component;
        }

        BaseComponent[] baseComponents = new ComponentBuilder()
                .append(Arrays.stream(components)
                        .flatMap(Arrays::stream)
                        .toArray(TextComponent[]::new))
                .create();

        return baseComponents;
    }

    private static String[] getPixelColors(String playerSkinUrl) {
        String[] colors = new String[64];
        try {
            BufferedImage skinImage = ImageIO.read(new URL(playerSkinUrl));

            int faceStartX = 8;
            int faceStartY = 8;
            int faceWidth = 8;
            int faceHeight = 8;

            BufferedImage faceImage = skinImage.getSubimage(faceStartX, faceStartY, faceWidth, faceHeight);

            int index = 0;
            for (int x = 0; x < faceHeight; x++) {
                for (int y = 0; y < faceWidth; y++) {
                    int rgb = faceImage.getRGB(x, y);
                    String hexColor = String.format("#%06X", (rgb & 0xFFFFFF));
                    colors[index++] = hexColor;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return colors;
    }

    private static String[] getPixelColorsMinotarNet(String uuid) {
        uuid = uuid.replace("-", "").strip();
        String[] colors = new String[64]; // 64
        try {
            BufferedImage skinImage = ImageIO.read(new URL("https://minotar.net/helm/"+uuid+"/8.png")); // playerSkinUrl

            int faceWidth = 8; // 8
            int faceHeight = 8; // 8

            int index = 0;
            for (int x = 0; x < faceHeight; x++) {
                for (int y = 0; y < faceWidth; y++) {
                    int rgb = skinImage.getRGB(x, y);
                    String hexColor = String.format("#%06X", (rgb & 0xFFFFFF));
                    colors[index++] = hexColor;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return colors;
    }


    private static String getPlayerSkinURL(UUID uuid) {
        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String jsonResponse = response.toString();
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray propertiesArray = jsonObject.getJSONArray("properties");
            for (int i = 0; i < propertiesArray.length(); i++) {
                JSONObject property = propertiesArray.getJSONObject(i);
                if (property.getString("name").equals("textures")) {
                    String value = property.getString("value");
                    byte[] decodedBytes = Base64.getDecoder().decode(value);
                    String decodedValue = new String(decodedBytes);
                    JSONObject textureJson = new JSONObject(decodedValue);

                    return textureJson.getJSONObject("textures").getJSONObject("SKIN").getString("url");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unable to retrieve player skin URL.";
    }

    private static String getPlayerSkinURLFromTextureAPI(UUID uuid) {
        try {

            String json = new String(Base64.getDecoder().decode(TextureAPI.getTexture(uuid).getBase64()));
            JSONObject jsonObject = new JSONObject(json);

            return jsonObject.getJSONObject("textures").getJSONObject("SKIN").getString("url");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unable to retrieve player skin URL.";
    }

    public static ChatHead getPlugin() {
        return plugin;
    }

}
