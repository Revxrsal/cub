package io.github.revxrsal.cub.jda.core;

import io.github.revxrsal.cub.CommandContext;
import io.github.revxrsal.cub.CommandHandler;
import io.github.revxrsal.cub.CommandSubject;
import io.github.revxrsal.cub.HandledCommand;
import io.github.revxrsal.cub.exception.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

final class DefaultExceptionHandler implements CommandExceptionHandler {

    private static final String VOWELS = "aeiou";
    public static final DefaultExceptionHandler INSTANCE = new DefaultExceptionHandler();

    @Override public void handleException(@NotNull CommandSubject sender,
                                          @NotNull CommandHandler commandHandler,
                                          @Nullable HandledCommand command,
                                          @NotNull List<String> arguments,
                                          @NotNull CommandContext context,
                                          @NotNull Throwable e, boolean async) {
        if (e instanceof InvalidValueException) {
            sender.reply("**Invalid " + ((InvalidValueException) e).getValueType().getId() + "**: " + e.getMessage());
        } else if (e instanceof InvalidCommandException) {
            sender.reply("**Invalid command**: " + ((InvalidCommandException) e).getInput());
        } else if (e instanceof MissingParameterException) {
            MissingParameterException mpe = (MissingParameterException) e;
            String article = VOWELS.indexOf(Character.toLowerCase(mpe.getParameter().getName().charAt(0))) != -1 ? "an" : "a";
            sender.reply("You must specify " + article + " " + mpe.getParameter().getName() + " !");
        } else if (e instanceof MissingPermissionException) {
            sender.reply("You do not have permission to execute this command!");
        } else if (e instanceof ResolverFailedException) {
            ResolverFailedException rfe = (ResolverFailedException) e;
            sender.reply("Cannot resolve " + rfe.getParameter().getName() + " from value " + rfe.getInput());
        } else if (e instanceof SimpleCommandException) {
            sender.reply(e.getMessage());
        } else if (e instanceof CooldownException) {
            sender.reply("You must wait **" + ((CooldownException) e).getTimeFancy() + "** before using this command again.");
        } else {
            sender.reply("An error occured while executing this command. Check console for details.");
            e.printStackTrace();
        }
    }
}
