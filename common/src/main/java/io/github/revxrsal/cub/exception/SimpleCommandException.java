package io.github.revxrsal.cub.exception;

/**
 * A command exception that is used for sending messages and stopping
 * command execution.
 */
public class SimpleCommandException extends CommandException {

    public SimpleCommandException(String message) {
        super(message);
    }
}
