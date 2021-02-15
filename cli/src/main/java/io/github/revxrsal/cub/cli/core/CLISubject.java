package io.github.revxrsal.cub.cli.core;

import io.github.revxrsal.cub.cli.ConsoleSubject;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.UUID;

class CLISubject implements ConsoleSubject {

    private static final String NAME = "Console";
    private static final UUID UUID = new UUID(0, 0);

    final PrintStream out;

    public CLISubject(PrintStream out) {
        this.out = out;
    }

    @Override public @NotNull String getName() {
        return NAME;
    }

    @Override public void reply(@NotNull String message) {
        out.println(message);
    }

    @Override public @NotNull UUID getUUID() {
        return UUID;
    }
}
