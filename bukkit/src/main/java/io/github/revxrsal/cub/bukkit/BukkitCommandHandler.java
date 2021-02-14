package io.github.revxrsal.cub.bukkit;

import io.github.revxrsal.cub.CommandHandler;
import io.github.revxrsal.cub.bukkit.annotation.TabCompletion;
import io.github.revxrsal.cub.bukkit.core.BukkitHandler;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static io.github.revxrsal.cub.core.Utils.n;

/**
 * Represents Bukkit's command handler implementation
 */
public interface BukkitCommandHandler extends CommandHandler {

    /**
     * Registers a {@link TabSuggestionProvider} for the specified ID, for use in commands
     * through the {@link TabCompletion} annotation.
     *
     * @param suggestionID The tab suggestion id
     * @param provider     The provider for this suggestion
     * @return This command handler
     */
    BukkitCommandHandler registerTabSuggestion(@NotNull String suggestionID, @NotNull TabSuggestionProvider provider);

    /**
     * Registers static completions for the specified ID, for use in commands
     * through the {@link TabCompletion} annotation.
     *
     * @param suggestionID The tab suggestion id
     * @param completions  The static list of suggestion. These will be copied and
     *                     will no longer be modifiable
     * @return This command handler
     */
    BukkitCommandHandler registerStaticTabSuggestion(@NotNull String suggestionID, @NotNull Collection<String> completions);

    /**
     * Registers static completions for the specified ID, for use in commands
     * through the {@link TabCompletion} annotation.
     *
     * @param suggestionID The tab suggestion id
     * @param completions  The static list of suggestion. These will be copied and
     *                     will no longer be modifiable
     * @return This command handler
     */
    BukkitCommandHandler registerStaticTabSuggestion(@NotNull String suggestionID, @NotNull String... completions);

    /**
     * Returns the plugin this command handler was registered for.
     *
     * @return The owning plugin
     */
    @NotNull Plugin getPlugin();

    /**
     * Creates a new {@link CommandHandler} for the specified plugin
     *
     * @param plugin Plugin to create for
     * @return The newly created command handler
     */
    static BukkitCommandHandler create(@NotNull Plugin plugin) {
        return new BukkitHandler(n(plugin, "plugin"));
    }

}
