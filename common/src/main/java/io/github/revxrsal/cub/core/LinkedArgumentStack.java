package io.github.revxrsal.cub.core;

import io.github.revxrsal.cub.ArgumentStack;
import io.github.revxrsal.cub.CommandHandler;
import io.github.revxrsal.cub.CommandParameter;
import io.github.revxrsal.cub.exception.MissingParameterException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

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

    @Override public String popForParameter(@NotNull CommandParameter parameter) {
        try {
            if (!parameter.consumesAllString()) return pop();
            String value = combine(" ");
            clear();
            return value;
        } catch (NoSuchElementException e) {
            throw new MissingParameterException(parameter, parameter.getResolver());
        }
    }

    @Override public @NotNull ArgumentStack copy() {
        LinkedArgumentStack stack = new LinkedArgumentStack(immutableArgsList, commandHandler);
        stack.addAll(this);
        return stack;
    }

    @Override public @NotNull ArgumentStack subList(int a, int b) {
        LinkedArgumentStack stack = new LinkedArgumentStack(immutableArgsList, commandHandler);
        stack.clear();
        for (int i = a; i < Math.min(size(), b); i++) {
            stack.add(get(i));
        }
        return stack;
    }
}
