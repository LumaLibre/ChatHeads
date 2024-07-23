package dev.jsinco.chatheads;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

@SuppressWarnings("deprecation")
public final class ChatHeadUtils {

    // Props to the genius, Minso, for this

    // Author: Minso, Jsinco
    public static BaseComponent[] getHead(Player player) {
        String[] hexColors = getPixelColorsFromSkin(player.getPlayerProfile().getTextures().getSkin(), true);

        if (hexColors.length < 64) {
            throw new IllegalArgumentException("Hex colors array must have at least 64 elements. (Image null or too small)");
        }

        TextComponent[][] components = new TextComponent[8][8];

        for (int i = 0; i < 64; i++) {
            int row = i / 8;
            int col = i % 8;
            char unicodeChar = (char) ('\uF000' + (i % 8) + 1);
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

    // Author: Minso
    public static String[] getPixelColorsFromSkin(URL playerSkinUrl, boolean overlay) {
        String[] colors = new String[64];
        try {
            BufferedImage skinImage = ImageIO.read(playerSkinUrl);

            int faceStartX = 8, faceStartY = 8;
            int faceWidth = 8, faceHeight = 8;

            int overlayStartX = 40;
            int overlayStartY = 8;

            BufferedImage faceImage = skinImage.getSubimage(faceStartX, faceStartY, faceWidth, faceHeight);
            BufferedImage overlayImage = skinImage.getSubimage(overlayStartX, overlayStartY, faceWidth, faceHeight);


            int index = 0;
            for (int x = 0; x < faceHeight; x++) {
                for (int y = 0; y < faceWidth; y++) {
                    int rgbFace = faceImage.getRGB(x, y);
                    int rgbOverlay = overlayImage.getRGB(x, y);

                    // Check if the overlay pixel is not transparent
                    if ((rgbOverlay >> 24) != 0x00 && overlay) {
                        colors[index++] = String.format("#%06X", (rgbOverlay & 0xFFFFFF)); // Use overlay color
                    } else {
                        colors[index++] = String.format("#%06X", (rgbFace & 0xFFFFFF)); // Use face color
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return colors; // Return the array containing the pixel colors
    }
}

/*
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
 */