package io.github.revxrsal.cub.exception;

import io.github.revxrsal.cub.CommandPermission;
import lombok.Getter;

/**
 * Thrown when the sender does not have permissions to execute the command
 */
@Getter
public class MissingPermissionException extends CommandException {

    /**
     * The permission missing
     */
    private final CommandPermission permission;

    public MissingPermissionException(CommandPermission permission) {
        this.permission = permission;
    }
}
