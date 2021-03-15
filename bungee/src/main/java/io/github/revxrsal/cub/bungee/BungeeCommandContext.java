package io.github.revxrsal.cub.bungee;

import io.github.revxrsal.cub.CommandContext;
import net.md_5.bungee.api.plugin.Command;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the command invocation context in Bungee
 */
public interface BungeeCommandContext extends CommandContext {

    /**
     * The command sender (subject)
     *
     * @return The command sender
     */
    @NotNull BungeeCommandSubject getSubject();

    /**
     * The root Bungee command invoked
     *
     * @return The root Bungee command
     */
    @NotNull Command getBungeeCommand();

}
