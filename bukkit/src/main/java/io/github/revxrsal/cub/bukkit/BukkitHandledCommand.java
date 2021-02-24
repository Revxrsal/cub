package io.github.revxrsal.cub.bukkit;

import io.github.revxrsal.cub.HandledCommand;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a Bukkit registered command
 */
public interface BukkitHandledCommand extends HandledCommand {

    /**
     * The tab completions for this command.
     *
     * @return The command completions
     */
    @NotNull List<TabSuggestionProvider> getTabCompletions();

}
