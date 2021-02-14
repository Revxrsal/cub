package io.github.revxrsal.cub.jda;

import io.github.revxrsal.cub.CommandHandler;
import io.github.revxrsal.cub.jda.core.JDAHandler;
import lombok.Builder;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

import static io.github.revxrsal.cub.core.Utils.n;

/**
 * Represents the command handler for JDA bots
 */
public interface JDACommandHandler extends CommandHandler {

    /**
     * The default settings set for the command handler.
     */
    Settings DEFAULT = Settings.builder().prefix("/").stripMarkdown(false).build();

    /**
     * Returns the JDA instance this command handler is running for
     *
     * @return The JDA instance
     */
    @NotNull JDA getJDA();

    /**
     * Returns the settings of this command handler
     *
     * @return The settings
     */
    @NotNull Settings getSettings();

    /**
     * Creates a new {@link JDACommandHandler} for the specified JDA instance
     * with the default settings.
     *
     * @param jda JDA to create for
     * @return The newly constructed command handler
     */
    static @NotNull JDACommandHandler create(@NotNull JDA jda) {
        return create(jda, DEFAULT);
    }

    /**
     * Creates a new {@link JDACommandHandler} for the specified JDA instance
     * and settings.
     *
     * @param jda      JDA to create for
     * @param settings The command handler settings
     * @return The newly constructed command handler
     */
    static @NotNull JDACommandHandler create(@NotNull JDA jda, @NotNull Settings settings) {
        n(jda, "JDA cannot be null!");
        n(settings, "Settings cannot be null!");
        return new JDAHandler(jda, settings);
    }

    /**
     * Represents settings for JDA command handlers.
     * <p>
     * This class is immutable, hence is thread-safe.
     */
    @Getter
    @Builder
    class Settings {

        /**
         * The command prefix
         */
        private final String prefix;

        /**
         * Whether should messages strip markdown or not
         */
        private final boolean stripMarkdown;

    }

}
