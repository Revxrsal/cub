package io.github.revxrsal.cub.exception;

import io.github.revxrsal.cub.CommandHelp;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Thrown when an invalid page is supplied in {@link CommandHelp#paginate(int, int)}.
 */
@Getter
@AllArgsConstructor
public class InvalidHelpPageException extends CommandException {

    private final CommandHelp<?> commandHelp;
    private final int page;

}
