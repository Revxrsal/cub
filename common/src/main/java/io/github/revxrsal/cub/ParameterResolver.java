package io.github.revxrsal.cub;

import io.github.revxrsal.cub.exception.CommandExceptionHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 * A resolver for a specific type of a {@link CommandParameter}.
 *
 * @param <R> The returned type
 * @param <A> The arguments type
 */
public interface ParameterResolver<A, R> {

    /**
     * Resolves a value for the specified parameter.
     *
     * @param args      The command arguments passed to the command.
     * @param subject   The command sender
     * @param parameter The parameter to resolve
     * @return The resolved result. May or may not be null.
     * @throws Throwable Any exceptions that should be handled by {@link CommandExceptionHandler}
     * @deprecated You should not implement this interface explicitly nor invoke this method
     * directly, and instead implement or cast to any of its subclasses: {@link ValueResolver} and {@link ContextResolver}.
     */
    @Deprecated
    R resolve(@NotNull A args, @NotNull CommandSubject subject, @NotNull CommandParameter parameter) throws Throwable;

    /**
     * A resolver for resolving values that are, by default, resolve-able through the command
     * invocation context, and do not need any data from the arguments to find the value.
     * An example context resolver is finding the sender's world.
     *
     * @param <T> The resolved type
     */
    interface ContextResolver<T> extends ParameterResolver<List<String>, T> {

        /**
         * Resolves the value of this resolver
         *
         * @param args      The command arguments passed to the command. You may not need those :)
         * @param subject   The command subject
         * @param parameter The parameter to resolve
         * @return The resolved value. May or may not be null.
         * @throws Throwable Any exceptions that should be handled by {@link CommandExceptionHandler}
         */
        T resolve(@NotNull @Unmodifiable List<String> args, @NotNull CommandSubject subject, @NotNull CommandParameter parameter) throws Throwable;

    }

    /**
     * A resolver for resolving values that, by default, require data from the arguments
     * to resolve their value.
     * An example context resolver is finding a player from their name.
     *
     * @param <T> The resolved type
     */
    interface ValueResolver<T> extends ParameterResolver<ArgumentStack, T> {

        /**
         * Resolves the value of this resolver
         *
         * @param args      The command arguments passed to the command.
         * @param subject   The command sender
         * @param parameter The parameter to resolve
         * @return The resolved value. May or may not be null.
         * @throws Throwable Any exceptions that should be handled by {@link CommandExceptionHandler}
         */
        @Contract(mutates = "param1")
        T resolve(@NotNull ArgumentStack args, @NotNull CommandSubject subject, @NotNull CommandParameter parameter) throws Throwable;
    }

}
