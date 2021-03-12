package io.github.revxrsal.cub;

/**
 * Represents a resolveable {@link HandledCommand} supplier. As in, commands
 * can have this as an argument if they want the user to specify
 * a {@link HandledCommand} just like any other command argument.
 */
public interface TargetHandledCommand {

    /**
     * The command resolved
     *
     * @return The command
     */
    HandledCommand getCommand();

}
