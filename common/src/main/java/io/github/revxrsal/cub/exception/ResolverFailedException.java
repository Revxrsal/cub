package io.github.revxrsal.cub.exception;

import io.github.revxrsal.cub.CommandParameter;
import io.github.revxrsal.cub.ParameterResolver;
import lombok.Getter;

/**
 * Thrown when a {@link ParameterResolver} fails to fetch the value. This can be due to multiple causes:
 * <ul>
 *     <li>The resolver considered the resolved value as "invalid"</li>
 *     <li>The resolver threw an exception, in which {@link ResolverFailedException} wraps it</li>
 * </ul>
 */
@Getter
public class ResolverFailedException extends CommandException {

    private final ParameterResolver<?, ?> resolver;
    private final Object input;
    private final CommandParameter parameter;
    private final Throwable parentCause;

    public ResolverFailedException(ParameterResolver<?, ?> resolver,
                                   Object input,
                                   CommandParameter parameter,
                                   Throwable parentCause) {
        this.resolver = resolver;
        this.input = input;
        this.parameter = parameter;
        this.parentCause = parentCause;
        initCause(parentCause);
    }

    public <T> ParameterResolver<?, T> getResolver() {
        return (ParameterResolver<?, T>) resolver;
    }

    public <T> T getInput() {
        return (T) input;
    }

}
