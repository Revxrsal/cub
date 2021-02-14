package io.github.revxrsal.cub.bukkit;

import io.github.revxrsal.cub.CommandContext;
import org.bukkit.command.Command;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the command invocation context in Bukkit
 */
public interface BukkitCommandContext extends CommandContext {

    /**
     * The command sender (subject)
     *
     * @return The command sender
     */
    @NotNull BukkitCommandSubject getSubject();

    /**
     * The root Bukkit command invoked
     *
     * @return The root Bukkit command
     */
    @NotNull Command getBukkitCommand();

}
