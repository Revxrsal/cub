package io.github.revxrsal.cub.bungee;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a Bungee registered command
 */
public interface BungeeHandledCommand {

    /**
     * The tab completions for this command.
     *
     * @return The command completions
     */
    @NotNull List<TabSuggestionProvider> getTabCompletions();

}
