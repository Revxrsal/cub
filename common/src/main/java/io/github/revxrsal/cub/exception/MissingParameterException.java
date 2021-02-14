package io.github.revxrsal.cub.exception;

import io.github.revxrsal.cub.CommandParameter;
import io.github.revxrsal.cub.ParameterResolver;
import lombok.Getter;

/**
 * Thrown when a (required) parameter is not supplied in the command
 */
@Getter
public class MissingParameterException extends CommandException {

    /**
     * The missing parameter
     */
    private final CommandParameter parameter;

    /**
     * The parameter's resolver
     */
    private final ParameterResolver<?, ?> resolver;

    public MissingParameterException(CommandParameter parameter, ParameterResolver<?, ?> resolver) {
        this.parameter = parameter;
        this.resolver = resolver;
    }

    public <T> ParameterResolver<?, T> getResolver() {
        return (ParameterResolver<?, T>) resolver;
    }
}
