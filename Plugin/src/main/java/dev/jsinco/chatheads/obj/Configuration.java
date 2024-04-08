package dev.jsinco.chatheads.obj;

import dev.jsinco.abstractjavafilelib.schemas.SnakeYamlConfig;

import java.util.List;

public final class Configuration {

    private static SnakeYamlConfig config = new SnakeYamlConfig("config.yml");

    public static String prefix = config.getString("prefix");
    public static List<String> avatarURLs = config.getStringList("avatarURLs");
    public static int imageSize = config.getInt("image-size");

    public static int useLink = 0;


    public static SnakeYamlConfig getConfig() {
        return config;
    }

    public static void reloadConfig() {
        config = new SnakeYamlConfig("config.yml");

        prefix = config.getString("prefix");
        avatarURLs = config.getStringList("avatarURLs");
        imageSize = config.getInt("image-size");

        useLink = 0;
    }

    public static void saveConfig() {
        config.save();
    }

}
