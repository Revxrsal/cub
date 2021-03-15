package io.github.revxrsal.cub.bungee;

import io.github.revxrsal.cub.CommandHandler;
import io.github.revxrsal.cub.bungee.annotation.TabCompletion;
import io.github.revxrsal.cub.bungee.core.BungeeHandler;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static io.github.revxrsal.cub.core.Utils.n;

/**
 * Represents Bungee's command handler implementation
 */
public interface BungeeCommandHandler extends CommandHandler {

    /**
     * Registers a {@link TabSuggestionProvider} for the specified ID, for use in commands
     * through the {@link TabCompletion} annotation.
     *
     * @param providerID The tab suggestion id
     * @param provider   The provider for this suggestion
     * @return This command handler
     */
    BungeeCommandHandler registerTabSuggestion(@NotNull String providerID, @NotNull TabSuggestionProvider provider);

    /**
     * Registers static completions for the specified ID, for use in commands
     * through the {@link TabCompletion} annotation.
     *
     * @param providerID  The tab suggestion id
     * @param completions The static list of suggestion. These will be copied and
     *                    will no longer be modifiable
     * @return This command handler
     */
    BungeeCommandHandler registerStaticTabSuggestion(@NotNull String providerID, @NotNull Collection<String> completions);

    /**
     * Registers static completions for the specified ID, for use in commands
     * through the {@link TabCompletion} annotation.
     *
     * @param providerID  The tab suggestion id
     * @param completions The static list of suggestion. These will be copied and
     *                    will no longer be modifiable
     * @return This command handler
     */
    BungeeCommandHandler registerStaticTabSuggestion(@NotNull String providerID, @NotNull String... completions);

    /**
     * Registers a {@link TabSuggestionProvider} for a specific parameter type. This way,
     * if the parameter is requested in the command, it will automatically be tab-completed
     * without having to be explicitly defined by a {@link TabCompletion}.
     *
     * @param parameterType The parameter type to complete
     * @param provider      The tab suggestion provider
     * @return This command handler
     */
    BungeeCommandHandler registerParameterTab(@NotNull Class<?> parameterType, @NotNull TabSuggestionProvider provider);

    /**
     * Registers a {@link TabSuggestionProvider} for a specific parameter type. This way,
     * if the parameter is requested in the command, it will automatically be tab-completed
     * without having to be explicitly defined by a {@link TabCompletion}.
     *
     * @param parameterType The parameter type to complete
     * @param providerID    The tab suggestion provider id. Must be registered with
     *                      either {@link #registerTabSuggestion(String, TabSuggestionProvider)}
     *                      or {@link #registerStaticTabSuggestion(String, String...)}.
     * @return This command handler
     */
    BungeeCommandHandler registerParameterTab(@NotNull Class<?> parameterType, @NotNull String providerID);

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
    static BungeeCommandHandler create(@NotNull Plugin plugin) {
        return new BungeeHandler(n(plugin, "plugin"));
    }

}
