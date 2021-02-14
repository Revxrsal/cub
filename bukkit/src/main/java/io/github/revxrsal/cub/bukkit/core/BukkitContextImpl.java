package io.github.revxrsal.cub.bukkit.core;

import io.github.revxrsal.cub.bukkit.BukkitCommandContext;
import io.github.revxrsal.cub.bukkit.BukkitCommandSubject;
import org.bukkit.command.Command;
import org.jetbrains.annotations.NotNull;

class BukkitContextImpl implements BukkitCommandContext {

    private final BukkitSubject subject;
    private final Command command;

    public BukkitContextImpl(BukkitSubject subject, Command command) {
        this.subject = subject;
        this.command = command;
    }

    @Override public @NotNull BukkitCommandSubject getSubject() {
        return subject;
    }

    @Override public @NotNull Command getBukkitCommand() {
        return command;
    }
}
