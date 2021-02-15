package io.github.revxrsal.cub.cli.core;

import io.github.revxrsal.cub.CommandContext;
import io.github.revxrsal.cub.core.BaseCommandHandler;
import io.github.revxrsal.cub.core.BaseDispatcher;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.Scanner;

class CLIDispatcher extends BaseDispatcher {

    public CLIDispatcher(BaseCommandHandler handler) {
        super(handler);
    }

    @Override protected boolean isPossibleSender(@NotNull Class<?> v) {
        return PrintStream.class.isAssignableFrom(v);
    }

    @Override protected Object handlePossibleSender(Class<?> type, @NotNull CommandContext context) {
        CLISubject subject = (CLISubject) context.getSubject();
        if (PrintStream.class.isAssignableFrom(type)) {
            return subject.out;
        }
        return null;
    }

     void begin(Scanner scanner) {
        while (scanner.hasNext()) {
            String input = scanner.nextLine();
            execute(((CLIHandler) handler).console, () -> ((CLIHandler) handler).console, input.split(" "));
        }
    }

}
