package io.github.revxrsal.cub.jda;

import io.github.revxrsal.cub.CommandContext;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the command execution context for JDA
 */
public interface JDACommandContext extends CommandContext {

    /**
     * Returns the command sender (subject)
     *
     * @return The command sender
     */
    @NotNull JDACommandSubject getSubject();

    /**
     * Returns the event in the invocation
     *
     * @return The event
     */
    @NotNull GuildMessageReceivedEvent getEvent();

    /**
     * Returns the message in the command
     *
     * @return The message
     */
    @NotNull Message getMessage();

}
