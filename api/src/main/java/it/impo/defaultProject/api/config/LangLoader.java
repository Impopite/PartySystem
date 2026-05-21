package it.impo.defaultProject.api.config;

import it.impo.defaultProject.api.config.constant.LangKey;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public abstract class LangLoader {

    public abstract Component getString(@NotNull LangKey prefix, @NotNull LangKey key);

    public abstract Component getString(@NotNull LangKey key);
}
