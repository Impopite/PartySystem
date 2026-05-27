package it.impo.partySystem.config;

import it.impo.partySystem.api.config.LangLoader;
import it.impo.partySystem.api.config.constant.LangKey;
import it.impo.partySystem.api.utils.HexUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.Configuration;
import org.jetbrains.annotations.NotNull;

public class BaseLangLoader extends LangLoader {

    private final Configuration langFile;

    public BaseLangLoader(Configuration langFile) {
        this.langFile = langFile;
    }

    @Override
    public Component getString(@NotNull LangKey prefix, @NotNull LangKey key) {
        String prefixStr = langFile.getString(prefix.getPath(), "");
        String keyStr    = langFile.getString(key.getPath(), "");
        return HexUtils.parse(prefixStr + keyStr);
    }

    @Override
    public Component getString(@NotNull LangKey key) {
        return HexUtils.parse(langFile.getString(key.getPath(), ""));
    }
}
