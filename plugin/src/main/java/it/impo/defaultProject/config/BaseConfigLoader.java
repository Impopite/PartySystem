package it.impo.defaultProject.config;

import it.impo.defaultProject.api.config.ConfigLoader;
import it.impo.defaultProject.api.config.LangLoader;
import it.impo.defaultProject.api.config.constant.ConfigKey;
import it.impo.defaultProject.config.base.CustomConfig;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.codehaus.plexus.util.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class BaseConfigLoader extends ConfigLoader {

    private final JavaPlugin plugin;

    @Getter
    private FileConfiguration config;
    private LangLoader langLoader;

    public BaseConfigLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() throws IOException {
        plugin.saveDefaultConfig();
        plugin.getDataFolder().mkdirs();

        config = plugin.getConfig();
        langLoader = initLang();

    }

    @Override
    public void reload() throws IOException {
        plugin.reloadConfig();

        config = plugin.getConfig();
        langLoader = initLang();
    }

    private @NotNull LangLoader initLang() throws IOException {
        loadDefaultLang();
        String currentLang = get(ConfigKey.LANG_FILE, "EN_us");

        File langFile = new File(plugin.getDataFolder(), "lang/" + currentLang + ".yml");
        if (!langFile.exists()) {
            plugin.getLogger().severe("[Lang] Lang file not found: " + currentLang + ". Use default EN_us.");
            currentLang = "EN_us";
        }

        CustomConfig langConfig = new CustomConfig(plugin, "lang/" + currentLang + ".yml");
        langConfig.loadConfig();
        plugin.getLogger().info("[Lang] Lingua caricata: " + langConfig.getCustomConfigFile().getName());
        return new BaseLangLoader(langConfig.getCustomConfig());
    }

    private void loadDefaultLang() {
        List<String> availableLang = List.of("EN_us", "IT_it");

        availableLang.forEach(defLang -> {
            String path = "lang/" + defLang + ".yml";
            if (!FileUtils.fileExists(plugin.getDataFolder().getAbsolutePath() + "/" + path)) plugin.saveResource(path, false);
        });
    }

    @Override
    public LangLoader getLangLoader() {
        return langLoader;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(@NotNull ConfigKey key, T defaultValue) {
        if (defaultValue == null) throw new IllegalArgumentException("defaultValue cannot be null for key: " + key.getPath());

        Object value = config.get(key.getPath());
        if (value == null) {
            warn(key, defaultValue);
            return defaultValue;
        }

        Class<T> expectedType = (Class<T>) defaultValue.getClass();
        if (!expectedType.isInstance(value)) {
            plugin.getLogger().severe(
                    "[Config] Wrong type for '" + key.getPath() +
                            "' → Expected: " + expectedType.getSimpleName() +
                            ", Got: " + value.getClass().getSimpleName() +
                            " → Using default: " + defaultValue
            );
            return defaultValue;
        }

        return expectedType.cast(value);
    }

    private void warn(ConfigKey key, Object def) {
        plugin.getLogger().warning(
                "[Config] Invalid or missing value in " +
                        key.getPath() +
                        " → Using default: " + def
        );
    }
}
