package io.github.revxrsal.cub.bukkit.core;

import io.github.revxrsal.cub.CommandContext;
import io.github.revxrsal.cub.CommandHandler;
import io.github.revxrsal.cub.CommandSubject;
import io.github.revxrsal.cub.HandledCommand;
import io.github.revxrsal.cub.bukkit.SenderNotPlayerException;
import io.github.revxrsal.cub.exception.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

final class DefaultExceptionHandler implements CommandExceptionHandler {

    public static final DefaultExceptionHandler INSTANCE = new DefaultExceptionHandler();

    @Override public String toString() {
        return "DefaultExceptionHandler";
    }

    @Override public void handleException(@NotNull CommandSubject sender,
                                          @NotNull CommandHandler commandHandler,
                                          @Nullable HandledCommand command,
                                          @NotNull List<String> arguments,
                                          @NotNull CommandContext context,
                                          @NotNull Throwable e,
                                          boolean async) {
        if (e instanceof InvalidValueException) {
            sender.reply("&cInvalid " + ((InvalidValueException) e).getValueType().getId() + ": &e" + e.getMessage());
        } else if (e instanceof SenderNotPlayerException) {
            sender.reply("&cYou must be a player to use this command!");
        } else if (e instanceof InvalidCommandException) {
            sender.reply("&cInvalid command: &e" + ((InvalidCommandException) e).getInput());
        } else if (e instanceof MissingParameterException) {
            MissingParameterException mpe = (MissingParameterException) e;
            sender.reply("&cYou must specify a(n) " + mpe.getParameter().getName() + "!");
        } else if (e instanceof MissingPermissionException) {
            sender.reply("&cYou do not have permission to execute this command!");
        } else if (e instanceof ResolverFailedException) {
            ResolverFailedException rfe = (ResolverFailedException) e;
            sender.reply("&cCannot resolve " + rfe.getParameter().getName() + " from value &e" + rfe.getInput());
        } else if (e instanceof SimpleCommandException) {
            sender.reply(e.getMessage());
        } else {
            sender.reply("&cAn error occured while executing this command. Check console for details.");
            e.printStackTrace();
        }
    }
}
