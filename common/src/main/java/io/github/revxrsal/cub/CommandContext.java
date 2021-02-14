package io.github.revxrsal.cub;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a command invocation context.
 * <p>
 * This clsas is bare-boned, and only provides the information common in all
 * platform contexts. To access more information relevant to your platform, cast
 * this context to the platform-appropriate context.
 */
public interface CommandContext {

    /**
     * Returns the command sender (subject)
     *
     * @return The command sender
     */
    @NotNull CommandSubject getSubject();

}
