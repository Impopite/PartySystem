package it.impo.partySystem.api.config;

import it.impo.partySystem.api.config.constant.ConfigKey;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public abstract class ConfigLoader {

    public abstract void load() throws IOException;

    public abstract void reload() throws IOException;

    public abstract LangLoader getLangLoader();

    public abstract <T> T get(@NotNull ConfigKey key, T defaultValue);

}
