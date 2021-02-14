package io.github.revxrsal.cub.exception;

import io.github.revxrsal.cub.CommandContext;
import io.github.revxrsal.cub.CommandHandler;
import io.github.revxrsal.cub.CommandSubject;
import io.github.revxrsal.cub.HandledCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A handler for all exceptions that may be thrown during the command
 * invocation.
 * <p>
 * Exceptions may be anything, including Java's normal exceptions,
 * and {@link CommandException}s thrown by different components in the framework.
 * <p>
 * Set with {@link CommandHandler#setExceptionHandler(CommandExceptionHandler)}.
 */
public interface CommandExceptionHandler {

    /**
     * Handles the given exception.
     *
     * @param sender         The command sender
     * @param commandHandler The command handler
     * @param command        The invoked command
     * @param arguments      The command arguments. These include default values
     * @param context        The command invocation context
     * @param throwable      The exception that was thrown
     * @param async          Whether was the command invoked asynchronously or
     *                       not
     */
    void handleException(@NotNull CommandSubject sender,
                         @NotNull CommandHandler commandHandler,
                         @Nullable HandledCommand command,
                         @NotNull List<String> arguments,
                         @NotNull CommandContext context,
                         @NotNull Throwable throwable,
                         boolean async);

}
