package io.github.revxrsal.cub;

import org.jetbrains.annotations.ApiStatus.AvailableSince;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.function.Predicate;

/**
 * A writer for generating entries for help commands.
 *
 * @param <T> The command help entry type. If we want to merely
 *            use strings as what is generated for each command,
 *            this would be a {@link String}.
 *            <p>
 *            Similarly, more complex types, such as chat components,
 *            can have this type as the appropriate chat component type.
 * @since 1.7.0
 */
public interface CommandHelpWriter<T> {

    /**
     * Generates a command help entry for the specified command
     *
     * @param command Command to generate for. It is generally advisable to
     *                do permission checks as well as other filters such
     *                as {@link HandledCommand#isPrivate()}.
     * @param subject Subject to generate for
     * @param args    The command arguments
     * @return The generated help entry. If null, this entry will not
     * appear on the generated help list.
     */
    @Nullable T generate(@NotNull HandledCommand command,
                         @NotNull CommandSubject subject,
                         @NotNull @Unmodifiable List<String> args);

    /**
     * Ignores any command that matches the specified predicate
     *
     * @param predicate Predicate to test for
     * @return The command help writer that filters according to
     * that predicate.
     */
    default CommandHelpWriter<T> ignore(@NotNull Predicate<HandledCommand> predicate) {
        return (command, subject, args) -> {
            if (predicate.test(command)) return null;
            return generate(command, subject, args);
        };
    }

    /**
     * Writes commands that only matches the specified predicate
     *
     * @param predicate Predicate to test for
     * @return The command help writer that only allows elements that match
     * the predicate.
     */
    default CommandHelpWriter<T> only(@NotNull Predicate<HandledCommand> predicate) {
        return ignore(predicate.negate());
    }

}
