package io.github.revxrsal.cub.core;

import io.github.revxrsal.cub.CommandHelp;
import io.github.revxrsal.cub.CommandParameter;
import io.github.revxrsal.cub.CommandSubject;
import io.github.revxrsal.cub.HandledCommand;
import io.github.revxrsal.cub.ParameterResolver.ContextResolver;
import io.github.revxrsal.cub.exception.InvalidHelpPageException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

import static io.github.revxrsal.cub.core.Utils.c;

final class BaseCommandHelp<T> extends ArrayList<T> implements CommandHelp<T> {

    @Override public CommandHelp<T> paginate(int page, int elementsPerPage) throws InvalidHelpPageException {
        if (isEmpty()) return new BaseCommandHelp<>();
        BaseCommandHelp<T> list = new BaseCommandHelp<T>();
        int size = getPageSize(elementsPerPage);
        if (page > size)
            throw new InvalidHelpPageException(this, page);
        int listIndex = page - 1;
        int l = Math.min(page * elementsPerPage, size());
        for (int i = listIndex * elementsPerPage; i < l; ++i) {
            list.add(get(i));
        }
        return list;
    }

    @Override public @Range(from = 1, to = Long.MAX_VALUE) int getPageSize(int elementsPerPage) {
        if (elementsPerPage < 1)
            throw new IllegalArgumentException("Elements per page cannot be less than 1! (" + elementsPerPage + ")");
        return (size() / elementsPerPage) + (size() % elementsPerPage == 0 ? 0 : 1);
    }

    public static final class Resolver implements ContextResolver<CommandHelp<?>> {

        private final BaseCommandHandler handler;

        public Resolver(BaseCommandHandler handler) {
            this.handler = handler;
        }

        @Override public CommandHelp<?> resolve(@NotNull @Unmodifiable List<String> args,
                                                @NotNull CommandSubject subject,
                                                @NotNull CommandParameter parameter) throws Throwable {
            c(handler.helpWriter, "No CommandHelpWriter is registered!");
            BaseCommandHelp<Object> entries = new BaseCommandHelp<>();
            HandledCommand command = parameter.getDeclaringCommand().getParent();
            if (command == null) command = parameter.getDeclaringCommand();
            for (HandledCommand subcommand : command.getSubcommands().values()) {
                if (subcommand == parameter.getDeclaringCommand())
                    continue; // don't include the help command in help menus
                Object entry = handler.helpWriter.generate(subcommand, subject, args);
                if (entry != null) {
                    entries.add(entry);
                }
            }
            return entries;
        }
    }
}
