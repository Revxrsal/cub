package io.github.revxrsal.cub.bukkit;

import io.github.revxrsal.cub.HandledCommand;
import io.github.revxrsal.cub.bukkit.core.BukkitHandler;
import org.bukkit.command.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A provider for tab completions.
 * <p>
 * Register with {@link BukkitHandler#registerTabSuggestion(String, TabSuggestionProvider)}
 */
public interface TabSuggestionProvider {

    /**
     * A {@link TabSuggestionProvider} that always returns an empty list.
     */
    TabSuggestionProvider EMPTY = (args, sender, command, bukkitCommand) -> Collections.emptyList();

    /**
     * Returns the suggestions
     *
     * @param args          The command arguments
     * @param sender        The command sender
     * @param command       The handled command
     * @param bukkitCommand Bukkit's {@link Command} for this command
     * @return The command suggestions.
     */
    @Nullable
    Collection<String> getSuggestions(@NotNull List<String> args,
                                      @NotNull BukkitCommandSubject sender,
                                      @NotNull HandledCommand command,
                                      @NotNull Command bukkitCommand) throws Throwable;

}
