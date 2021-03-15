package io.github.revxrsal.cub.bungee.core;

import io.github.revxrsal.cub.bungee.BungeeCommandContext;
import io.github.revxrsal.cub.bungee.BungeeCommandSubject;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.plugin.Command;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
class BungeeContextImpl implements BungeeCommandContext {

    private final BungeeCommandSubject subject;
    private final Command command;

    @Override public @NotNull BungeeCommandSubject getSubject() {
        return subject;
    }

    @Override public @NotNull Command getBungeeCommand() {
        return command;
    }
}
