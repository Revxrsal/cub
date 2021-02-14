package io.github.revxrsal.cub.jda.core;

import io.github.revxrsal.cub.CommandContext;
import io.github.revxrsal.cub.core.BaseDispatcher;
import io.github.revxrsal.cub.jda.JDACommandContext;
import io.github.revxrsal.cub.jda.JDACommandHandler.Settings;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

class JDADispatcher extends BaseDispatcher implements EventListener {

    private final Settings settings;

    public JDADispatcher(JDAHandler handler) {
        super(handler);
        settings = handler.getSettings();
    }

    @Override public void onEvent(@NotNull GenericEvent genericEvent) {
        if (!(genericEvent instanceof GuildMessageReceivedEvent)) return;
        GuildMessageReceivedEvent event = (GuildMessageReceivedEvent) genericEvent;
        if (event.isWebhookMessage()) return;
        if (event.getAuthor().isBot()) return;
        String content = getContent(event.getMessage());
        if (!content.startsWith(settings.getPrefix())) return;
        JDASubject subject = new JDASubject(event);
        JDAContext context = new JDAContext(subject);
        execute(subject, context, content.substring(settings.getPrefix().length()).split(" "));
    }

    private String getContent(Message message) {
        return settings.isStripMarkdown() ? message.getContentStripped() : message.getContentRaw();
    }

    @Override protected boolean isPossibleSender(@NotNull Class<?> v) {
        return Member.class.isAssignableFrom(v) ||
                User.class.isAssignableFrom(v) ||
                GuildMessageReceivedEvent.class.isAssignableFrom(v) ||
                MessageChannel.class.isAssignableFrom(v) ||
                Guild.class.isAssignableFrom(v);
    }

    @Override protected Object handlePossibleSender(Class<?> type, @NotNull CommandContext commandContext) {
        JDACommandContext context = (JDACommandContext) commandContext;
        if (Member.class.isAssignableFrom(type))
            return context.getSubject().asMember();
        if (User.class.isAssignableFrom(type))
            return context.getSubject().asUser();
        if (GuildMessageReceivedEvent.class.isAssignableFrom(type))
            return context.getSubject().getParentEvent();
        if (MessageChannel.class.isAssignableFrom(type))
            return context.getSubject().getParentEvent().getChannel();
        if (Guild.class.isAssignableFrom(type))
            return context.getSubject().getParentEvent().getGuild();
        return null;
    }
}
