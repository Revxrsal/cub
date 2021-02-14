package io.github.revxrsal.cub;

import io.github.revxrsal.cub.exception.CommandExceptionHandler;
import org.jetbrains.annotations.NotNull;

/**
 * A validator for a specific parameter type. These validators can do extra checks on parameters
 * after they are resolved from {@link ParameterResolver}s.
 *
 * @param <T> The parameter handler
 */
public interface ParameterValidator<T> {

    /**
     * Validates the specified value that was passed to a parameter.
     * <p>
     * Ideally, a validator will want to throw an exception when the parameter is
     * not valid, and then further handled with {@link CommandExceptionHandler}.
     *
     * @param value     The parameter value. May or may not be null, depending on the resolver.
     * @param parameter The parameter that will take this value
     * @param subject   The command subject
     * @throws Throwable Any throwable this validator will throw.
     */
    void validate(T value, @NotNull CommandParameter parameter, @NotNull CommandSubject subject) throws Throwable;

}
