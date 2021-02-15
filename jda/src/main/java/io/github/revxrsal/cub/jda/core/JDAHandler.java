package io.github.revxrsal.cub.jda.core;

import io.github.revxrsal.cub.ArgumentStack;
import io.github.revxrsal.cub.CommandHandler;
import io.github.revxrsal.cub.CommandParameter;
import io.github.revxrsal.cub.CommandSubject;
import io.github.revxrsal.cub.ParameterResolver.ValueResolver;
import io.github.revxrsal.cub.core.BaseCommandHandler;
import io.github.revxrsal.cub.exception.InvalidValueException;
import io.github.revxrsal.cub.exception.InvalidValueException.ValueType;
import io.github.revxrsal.cub.jda.JDACommandHandler;
import io.github.revxrsal.cub.jda.JDACommandSubject;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JDAHandler extends BaseCommandHandler implements JDACommandHandler {

    private static final ValueType USER = new ValueType("user");
    private static final ValueType CHANNEL = new ValueType("channel");
    private static final ValueType ROLE = new ValueType("role");

    private static final Pattern SNOWFLAKE = Pattern.compile("<(@|@!|&|#)(?<snowflake>\\d{18})>");

    private final JDA jda;
    private final Settings settings;

    public JDAHandler(JDA jda, Settings settings) {
        super();
        this.jda = jda;
        this.settings = settings;
        jda.addEventListener(new JDADispatcher(this));
        setExceptionHandler(DefaultExceptionHandler.INSTANCE);
        registerContextResolver(JDACommandSubject.class, (args, sender, parameter) -> (JDACommandSubject) sender);
        registerContextResolver(SelfUser.class, (args, subject, parameter) -> ((JDACommandSubject) subject).getParentEvent().getJDA().getSelfUser());
        registerContextResolver(JDA.class, (args, subject, parameter) -> jda);
        registerDependency(JDA.class, jda);
        registerTypeResolver(User.class, (args, csubject, parameter) -> {
            JDACommandSubject subject = (JDACommandSubject) csubject;
            String supplied = args.popForParameter(parameter);
            if (supplied.equalsIgnoreCase("me"))
                return subject.asUser();
            Matcher matcher = SNOWFLAKE.matcher(supplied); // test for mentions
            if (matcher.find()) {
                return jda.getUserById(matcher.group(2));
            } else {
                return jda.getUsersByName(supplied, true).stream().findFirst()
                        .orElseThrow(() -> new InvalidValueException(USER, subject));
            }
        });
        registerTypeResolver(GuildChannel.class, new ChannelResolver<>(Guild::getGuildChannelById, null));
        registerTypeResolver(TextChannel.class, new ChannelResolver<>(Guild::getTextChannelById, Guild::getTextChannelsByName));
        registerTypeResolver(VoiceChannel.class, new ChannelResolver<>(Guild::getVoiceChannelById, Guild::getVoiceChannelsByName));
        registerTypeResolver(Member.class, new SnowflakeResolver<>(Guild::getMemberById, Guild::getMembersByEffectiveName, USER));
        registerTypeResolver(Role.class, new SnowflakeResolver<>(Guild::getRoleById, Guild::getRolesByName, ROLE));
    }

    @Override public @NotNull JDA getJDA() {
        return jda;
    }

    @Override public @NotNull Settings getSettings() {
        return settings;
    }

    @Override public CommandHandler registerCommand(@NotNull Object instance) {
        addCommand(new JDACommand(this, instance, null, null));
        setDependencies(instance);
        return this;
    }

    @AllArgsConstructor
    private static class ChannelResolver<T> implements ValueResolver<T> {

        private final BiFunction<Guild, String, T> idToChannel;
        private @Nullable final F<T> byId;

        @Override public T resolve(@NotNull ArgumentStack args, @NotNull CommandSubject csubject, @NotNull CommandParameter parameter) throws Throwable {
            String supplied = args.popForParameter(parameter);
            Matcher matcher = SNOWFLAKE.matcher(supplied); // test for mentions
            if (matcher.find()) {
                return idToChannel.apply(((JDACommandSubject) csubject).getParentEvent().getGuild(), matcher.group(2));
            }
            if (byId != null)
                return byId.find(((JDACommandSubject) csubject).getParentEvent().getGuild(), supplied, true)
                        .stream().findFirst().orElseThrow(() -> new InvalidValueException(CHANNEL, supplied));
            throw new InvalidValueException(CHANNEL, supplied);
        }
    }

    @AllArgsConstructor
    private static class SnowflakeResolver<T extends ISnowflake> implements ValueResolver<T> {

        private final BiFunction<Guild, String, T> idToSnowflake;
        private @Nullable final F<T> byId;
        private final ValueType type;

        @Override public T resolve(@NotNull ArgumentStack args, @NotNull CommandSubject csubject, @NotNull CommandParameter parameter) throws Throwable {
            String supplied = args.popForParameter(parameter);
            Matcher matcher = SNOWFLAKE.matcher(supplied); // test for mentions
            if (matcher.find()) {
                return idToSnowflake.apply(((JDACommandSubject) csubject).getParentEvent().getGuild(), matcher.group(2));
            }
            if (byId != null)
                return byId.find(((JDACommandSubject) csubject).getParentEvent().getGuild(), supplied, true)
                        .stream().findFirst().orElseThrow(() -> new InvalidValueException(type, supplied));
            throw new InvalidValueException(type, supplied);
        }
    }

    private interface F<T> {

        List<T> find(Guild guild, String id, boolean ignoreCase);
    }

}
