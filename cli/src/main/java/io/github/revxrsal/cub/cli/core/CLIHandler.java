package io.github.revxrsal.cub.cli.core;

import io.github.revxrsal.cub.cli.ConsoleCommandHandler;
import io.github.revxrsal.cub.cli.ConsoleSubject;
import io.github.revxrsal.cub.core.BaseCommandHandler;

import java.io.PrintStream;
import java.util.Scanner;

public final class CLIHandler extends BaseCommandHandler implements ConsoleCommandHandler {

    final Scanner reader;
    final PrintStream out;
    final CLISubject console;
    final CLIDispatcher dispatcher;

    public CLIHandler(Scanner reader, PrintStream out) {
        super();
        setExceptionHandler(DefaultExceptionHandler.INSTANCE);
        this.reader = reader;
        this.out = out;
        console = new CLISubject(out);
        dispatcher = new CLIDispatcher(this);
        registerContextResolver(ConsoleSubject.class, (args, subject, parameter) -> (ConsoleSubject) subject);
        registerDependency(ConsoleSubject.class, console);
        registerDependency(PrintStream.class, out);
        registerDependency(Scanner.class, reader);
    }

    @Override public void requestInput() {
        dispatcher.begin(reader);
    }

    @Override public ConsoleSubject getSubject() {
        return console;
    }
}
