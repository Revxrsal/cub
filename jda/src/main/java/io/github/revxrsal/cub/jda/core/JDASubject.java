package io.github.revxrsal.cub.jda.core;

import io.github.revxrsal.cub.jda.JDACommandSubject;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

class JDASubject implements JDACommandSubject {

    private final GuildMessageReceivedEvent event;
    private final UUID uuid;

    public JDASubject(GuildMessageReceivedEvent event) {
        this.event = event;
        uuid = new UUID(0, event.getAuthor().getIdLong());
    }

    @Override public String getName() {
        return event.getAuthor().getName();
    }

    @Override public void reply(@NotNull String message) {
        event.getChannel().sendMessage(message).queue();
    }

    @Override public UUID getUUID() {
        return uuid;
    }

    @Override public @NotNull Member asMember() {
        return event.getMember();
    }

    @Override public @NotNull User asUser() {
        return event.getAuthor();
    }

    @Override public @NotNull GuildMessageReceivedEvent getParentEvent() {
        return event;
    }
}
