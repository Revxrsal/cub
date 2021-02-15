package io.github.revxrsal.cub;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Deque;
import java.util.List;

/**
 * The arguments passed to a command.
 * <p>
 * This class holds very similar functionality to {@link java.util.LinkedList}, and
 * any resovlers that get an instance of this can use {@link ArgumentStack#pop()} to get
 * the string they want to use.
 */
public interface ArgumentStack extends Deque<String>, List<String> {

    /**
     * The initial arguments, as an immutable list. These will never be modified even
     * if this stack was modified previously.
     *
     * @return The arguments.
     */
    @NotNull @Unmodifiable List<String> asImmutableList();

    /**
     * Combines all present arguments in this stack
     *
     * @param delimiter Delimiter between these arguments.
     * @return The combined string
     */
    @NotNull String combine(String delimiter);

    /**
     * Combines all present arguments in this stack, starting from
     * the specified index
     *
     * @param delimiter  Delimiter between these arguments
     * @param startIndex The start index to combine from
     * @return The combined string
     */
    @NotNull String combine(@NotNull String delimiter, int startIndex);

    /**
     * The command handler that instantiated this argument stack.
     *
     * @return The command handler
     */
    @NotNull CommandHandler getCommandHandler();

    /**
     * Returns (and removes) the string in which might get concatenated with the rest
     * of the arguments if the parameter {@link CommandParameter#consumesAllString() consumes all strings}
     * that follow it.
     *
     * @param parameter The parameter to get for
     * @return The string for this parameter. Will return the first argument if the parameter cannot
     * consume all strings.
     */
    String popForParameter(@NotNull CommandParameter parameter);

    /**
     * Creates an identical copy of this argument stack. This copy will behave
     * independently of this agrument stack.
     *
     * @return The copy
     */
    @NotNull ArgumentStack copy();

    /**
     * Creates a sub-list of this argument stack
     *
     * @param startIndex The index to start copying from, inclusive
     * @param endIndex   The index to stop, exclusive.
     * @return The newly created sub-argument stack
     */
    @NotNull ArgumentStack subList(int startIndex, int endIndex);

}
