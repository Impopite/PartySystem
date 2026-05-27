package it.impo.partySystem.config.base;

import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ResourceConfig {

    private final JavaPlugin plugin;
    private final String directory;
    private final Boolean replace;

    @Getter
    private File customConfigFile;

    @Getter
    private FileConfiguration customConfig;

    public ResourceConfig(JavaPlugin plugin, String directory, Boolean replace) {
        this.plugin = plugin;
        this.directory = directory;
        this.replace = replace;

        loadConfig();
    }

    public void loadConfig() {
        customConfigFile = new File(plugin.getDataFolder() + File.separator + directory.replace("/", File.separator));

        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            plugin.saveResource(directory, replace);
        }

        customConfig = new YamlConfiguration();

        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().severe("Failed to load config: " + e.getMessage());
        }

        saveConfig();
    }

    public void reloadConfig() {
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveConfig() {

        try {
            customConfig.save(customConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
