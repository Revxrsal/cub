package io.github.revxrsal.cub.exception;

import lombok.Getter;

/**
 * Thrown when an invalid (sub)command is requested.
 */
@Getter
public class InvalidCommandException extends CommandException {

    /**
     * The string that the command was searched with
     */
    private final String input;

    public InvalidCommandException(String input) {
        super(input);
        this.input = input;
    }
}
