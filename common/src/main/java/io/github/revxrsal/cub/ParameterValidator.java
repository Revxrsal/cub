package io.github.revxrsal.cub;

import io.github.revxrsal.cub.exception.CommandExceptionHandler;
import org.jetbrains.annotations.NotNull;

/**
 * A validator for a specific parameter type. These validators can do extra checks on parameters
 * after they are resolved from {@link ParameterResolver}s.
 * <p>
 * Validators work on subclasses as well. For example, we can write a validator to validate
 * a custom <code>@Range(min, max)</code> annotation for numbers:
 *
 * <pre>{@code
 * public class RangeValidator implements ParameterValidator<Number> {
 *
 *     public void validate(Number value, @NotNull CommandParameter parameter, @NotNull CommandSubject sender) {
 *         Range range = parameter.getAnnotation(Range.class);
 *         if (range == null) return;
 *         if (value.doubleValue() < range.min())
 *             throw new SimpleCommandException(parameter.getName() + " must be more than " + range.min());
 *         if (value.doubleValue() > range.max())
 *             throw new SimpleCommandException(parameter.getName() + " must be less than " + range.max());
 *     }
 * }
 * }</pre>
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
