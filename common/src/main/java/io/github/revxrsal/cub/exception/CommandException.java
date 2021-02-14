package io.github.revxrsal.cub.exception;

/**
 * Represents an exception that was thrown during, before or after the command invocation.
 * <p>
 * These are to be handled by the {@link CommandExceptionHandler}.
 *
 * @see MissingParameterException
 * @see ResponseFailedException
 * @see InvalidCommandException
 * @see ResolverFailedException
 * @see InvalidValueException
 * @see CommandExceptionHandler
 */
public class CommandException extends RuntimeException {

    public CommandException() {
    }

    public CommandException(String message) {
        super(message);
    }

    public CommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandException(Throwable cause) {
        super(cause);
    }

    protected CommandException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
