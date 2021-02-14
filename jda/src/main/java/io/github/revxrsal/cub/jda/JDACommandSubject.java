package io.github.revxrsal.cub.jda;

import io.github.revxrsal.cub.CommandSubject;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the command sender in JDA
 */
public interface JDACommandSubject extends CommandSubject {

    /**
     * Returns this sender as a {@link Member}.
     *
     * @return This sender as a member
     */
    @NotNull Member asMember();

    /**
     * Returns this sender as a {@link User}
     *
     * @return This sender as a user
     */
    @NotNull User asUser();

    /**
     * Returns the event for this subject
     *
     * @return The event
     */
    @NotNull GuildMessageReceivedEvent getParentEvent();

}
