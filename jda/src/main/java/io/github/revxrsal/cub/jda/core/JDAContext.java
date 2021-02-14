package io.github.revxrsal.cub.jda.core;

import io.github.revxrsal.cub.jda.JDACommandContext;
import io.github.revxrsal.cub.jda.JDACommandSubject;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

class JDAContext implements JDACommandContext {

    private final JDASubject subject;

    public JDAContext(JDASubject subject) {
        this.subject = subject;
    }

    @Override public @NotNull JDACommandSubject getSubject() {
        return subject;
    }

    @Override public @NotNull GuildMessageReceivedEvent getEvent() {
        return subject.getParentEvent();
    }

    @Override public @NotNull Message getMessage() {
        return subject.getParentEvent().getMessage();
    }
}
