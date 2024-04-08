package dev.jsinco.chatheads;

import dev.jsinco.chatheads.obj.Configuration;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SuppressWarnings("deprecation")
public final class ChatHeadUtils {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();


    // Author: Minso
    public static BaseComponent[] getChatHead(UUID uuid) {
        String[] hexColors = getAvatarAsStringArray(uuid.toString());

        if (hexColors == null || hexColors.length < 64) {
            throw new IllegalArgumentException("Hex colors array must have at least 64 elements. (Image null or too small)");
        }

        TextComponent[][] components = new TextComponent[8][8];

        for (int i = 0; i < 64; i++) {
            int row = i / 8;
            int col = i % 8;
            char unicodeChar = (char) ('\uF000' + (i % 8) + 1);
            char spaceChar;
            TextComponent component = new TextComponent();
            if (i == 7 || i == 15 || i == 23 || i == 31 || i == 39 || i == 47 || i == 55) {
                component.setText(Character.toString(unicodeChar) + '\uF101');
            } else if (i == 63) {
                component.setText(Character.toString(unicodeChar));
            } else {
                component.setText(Character.toString(unicodeChar) + '\uF102');
            }

            component.setColor(ChatColor.of(hexColors[i]));
            component.setFont("minecraft:playerhead");
            components[row][col] = component;
        }

        return new ComponentBuilder()
                .append(Arrays.stream(components)
                        .flatMap(Arrays::stream)
                        .toArray(TextComponent[]::new))
                .create();
    }

    // Author: Minso & Jsinco
    private static String[] getAvatarAsStringArray(String uuid) {
        if (Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Cannot fetch image on main thread");
        }
        int size = Configuration.imageSize;
        String[] colors = new String[size*size];

        String url = Configuration.avatarURLs.get(Configuration.useLink)
                .replace("{uuid}", uuid)
                .replace("{uuid-no-dashes}", uuid.replace("-", "").strip())
                .replace("{size}", String.valueOf(size));

        BufferedImage skinImage = fetchImageWithTimeout(url, 5, TimeUnit.SECONDS);
        if (skinImage == null) {
            return null;
        }

        int index = 0;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int rgb = skinImage.getRGB(x, y);
                String hexColor = String.format("#%06X", (rgb & 0xFFFFFF));
                colors[index++] = hexColor;
            }
        }
        return colors;
    }

    // Author: Jsinco
    private static BufferedImage fetchImageWithTimeout(String urlString, long timeout, TimeUnit timeUnit) {
        Callable<BufferedImage> imageFetchTask = () -> ImageIO.read(new URL(urlString));

        Future<BufferedImage> future = executor.submit(imageFetchTask);
        try {
            return future.get(timeout, timeUnit);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            future.cancel(true); // Interrupt the image reading if it's still running
            e.printStackTrace();
            Configuration.useLink++;
            return null;
        }
    }
}
