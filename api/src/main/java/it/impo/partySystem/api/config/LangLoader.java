package it.impo.partySystem.api.config;

import it.impo.partySystem.api.config.constant.LangKey;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public abstract class LangLoader {

    public abstract Component getString(@NotNull LangKey prefix, @NotNull LangKey key);

    public abstract Component getString(@NotNull LangKey key);
}
