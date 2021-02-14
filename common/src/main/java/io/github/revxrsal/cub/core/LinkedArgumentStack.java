package io.github.revxrsal.cub.core;

import io.github.revxrsal.cub.ArgumentStack;
import io.github.revxrsal.cub.CommandHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

public final class LinkedArgumentStack extends LinkedList<String> implements ArgumentStack {

    private final List<String> immutableArgsList;
    private final CommandHandler commandHandler;

    public LinkedArgumentStack(CommandHandler commandHandler, @NotNull String[] args) {
        Collections.addAll(this, args);
        immutableArgsList = Utils.immutable(args);
        this.commandHandler = commandHandler;
    }

    private LinkedArgumentStack(List<String> immutableArgsList, CommandHandler commandHandler) {
        this.immutableArgsList = immutableArgsList;
        this.commandHandler = commandHandler;
    }

    @Override public @NotNull @Unmodifiable List<String> asImmutableList() {
        return immutableArgsList;
    }

    @Override public @NotNull String combine(String delimiter) {
        return String.join(delimiter, this);
    }

    @Override public @NotNull String combine(@NotNull String delimiter, int startIndex) {
        StringJoiner joiner = new StringJoiner(delimiter);
        for (int i = startIndex; i < size(); i++)
            joiner.add(get(i));
        return joiner.toString();
    }

    @Override public @NotNull CommandHandler getCommandHandler() {
        return commandHandler;
    }

    @Override public @NotNull ArgumentStack copy() {
        LinkedArgumentStack stack = new LinkedArgumentStack(immutableArgsList, commandHandler);
        stack.addAll(this);
        return stack;
    }
}
