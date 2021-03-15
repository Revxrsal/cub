package io.github.revxrsal.cub.bungee.core;

import io.github.revxrsal.cub.CommandContext;
import io.github.revxrsal.cub.CommandHandler;
import io.github.revxrsal.cub.CommandSubject;
import io.github.revxrsal.cub.HandledCommand;
import io.github.revxrsal.cub.bungee.SenderNotPlayerException;
import io.github.revxrsal.cub.exception.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

final class DefaultExceptionHandler implements CommandExceptionHandler {

    private static final String VOWELS = "aeiou";
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
            String article = VOWELS.indexOf(Character.toLowerCase(mpe.getParameter().getName().charAt(0))) != -1 ? "an" : "a";
            sender.reply("&cYou must specify " + article + " " + mpe.getParameter().getName() + "!");
        } else if (e instanceof MissingPermissionException) {
            sender.reply("&cYou do not have permission to execute this command!");
        } else if (e instanceof ResolverFailedException) {
            ResolverFailedException rfe = (ResolverFailedException) e;
            sender.reply("&cCannot resolve " + rfe.getParameter().getName() + " from value &e" + rfe.getInput());
        } else if (e instanceof SimpleCommandException) {
            sender.reply(e.getMessage());
        } else if (e instanceof CooldownException) {
            sender.reply("&cYou must wait &e" + ((CooldownException) e).getTimeFancy() + " &cbefore using this command again.");
        } else {
            sender.reply("&cAn error occured while executing this command. Check console for details.");
            e.printStackTrace();
        }
    }
}
