package it.impo.defaultProject.config.base;

import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class CustomConfig {

    private final JavaPlugin plugin;

    @Getter
    private final File customConfigFile;

    @Getter
    private FileConfiguration customConfig;

    public CustomConfig(@NotNull JavaPlugin plugin, String directory) {
        this.plugin = plugin;
        this.customConfigFile = new File(plugin.getDataFolder(), directory);
    }

    public void loadConfig() throws IOException {

        plugin.getDataFolder().mkdirs();
        if (!customConfigFile.exists()) customConfigFile.createNewFile();

        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);

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
