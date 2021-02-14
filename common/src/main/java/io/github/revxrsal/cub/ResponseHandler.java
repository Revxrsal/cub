package io.github.revxrsal.cub;

import org.jetbrains.annotations.NotNull;

/**
 * A handler for post-handling command responses (results returned from the
 * command methods)
 *
 * @param <T> The response type
 */
public interface ResponseHandler<T> {

    /**
     * The response handler for methods that return void
     */
    ResponseHandler<?> VOID = (response, sender, command, context) -> {
    };

    /**
     * Handles the response returned from the method
     *
     * @param response The response returned from the method. May or may
     *                 not be null.
     * @param subject  The sender of the command
     * @param command  The invoked command
     * @param context  The command invocation context
     */
    void handleResponse(T response,
                        @NotNull CommandSubject subject,
                        @NotNull HandledCommand command,
                        @NotNull CommandContext context);

}
