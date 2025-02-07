package dev.jsinco.chatheads;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public final class ChatHeadUtils {

    private static final Key FONT_KEY = Key.key("minecraft", "playerhead");

    // Author: Minso, Jsinco
    public static Component getHead(Player player) {
        String[] hexColors = getPixelColorsFromSkin(player.getPlayerProfile().getTextures().getSkin(), true);

        if (hexColors.length < 64) {
            throw new IllegalArgumentException("Hex colors array must have at least 64 elements. (Image null or too small)");
        }

        Component[][] components = new Component[8][8];

        for (int i = 0; i < 64; i++) {
            int row = i / 8;
            int col = i % 8;
            char unicodeChar = (char) ('\uF000' + (i % 8) + 1);
            String text;
            if (i == 7 || i == 15 || i == 23 || i == 31 || i == 39 || i == 47 || i == 55) {
                text = Character.toString(unicodeChar) + '\uF101';
            } else if (i == 63) {
                text = Character.toString(unicodeChar);
            } else {
                text = Character.toString(unicodeChar) + '\uF102';
            }

            Component component = Component.text(text)
                    .color(TextColor.fromHexString(hexColors[i]))
                    .font(FONT_KEY);
            components[row][col] = component;
        }

        TextComponent.Builder builder = Component.text();
        for (Component[] row : components) {
            builder.append(Component.text().append(row));
        }
        return builder.build();
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