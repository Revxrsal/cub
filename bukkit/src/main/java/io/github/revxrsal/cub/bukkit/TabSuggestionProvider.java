package io.github.revxrsal.cub.bukkit;

import io.github.revxrsal.cub.HandledCommand;
import io.github.revxrsal.cub.bukkit.core.BukkitHandler;
import io.github.revxrsal.cub.core.Utils;
import org.bukkit.command.Command;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
    @NotNull
    Collection<String> getSuggestions(@NotNull List<String> args,
                                      @NotNull BukkitCommandSubject sender,
                                      @NotNull HandledCommand command,
                                      @NotNull Command bukkitCommand) throws Throwable;

    /**
     * Composes the two {@link TabSuggestionProvider}s into one provider that returns
     * the completions from both.
     *
     * @param other Other provider to merge with
     * @return The new provider
     */
    @Contract("null -> this; !null -> !null")
    default TabSuggestionProvider compose(@Nullable TabSuggestionProvider other) {
        if (other == null) return this;
        return (args, sender, command, bukkitCommand) -> {
            Set<String> completions = new HashSet<>(other.getSuggestions(args, sender, command, bukkitCommand));
            completions.addAll(getSuggestions(args, sender, command, bukkitCommand));
            return completions;
        };
    }
}
