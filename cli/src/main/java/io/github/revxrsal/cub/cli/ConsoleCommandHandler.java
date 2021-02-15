package io.github.revxrsal.cub.cli;

import io.github.revxrsal.cub.CommandHandler;
import io.github.revxrsal.cub.cli.core.CLIHandler;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static io.github.revxrsal.cub.core.Utils.n;

/**
 * Represents the command-line (CLI) command handler.
 * <p>
 * It's important to call {@link #requestInput()} after creating and registering
 * everything.
 */
public interface ConsoleCommandHandler extends CommandHandler {

    /**
     * Starts asking the user for input
     * <p>
     * Note that this should only be invoked after everything has been
     * fully registered.
     */
    void requestInput();

    /**
     * Returns the singleton console instance for this command handler.
     *
     * @return The singleton console
     */
    ConsoleSubject getSubject();

    /**
     * Creates a new command handler for the specified that uses the
     * default {@link System#in} and {@link System#out} for taking input
     * and sending messages
     *
     * @return The newly created command handler.
     */
    static @NotNull ConsoleCommandHandler create() {
        return create(System.in, System.out);
    }

    /**
     * Creates a new command handler for the specified input stream,
     * and uses {@link System#out} for the output.
     *
     * @param in The input stream to fetch data from.
     * @return The newly created command handler.
     */
    static @NotNull ConsoleCommandHandler create(@NotNull InputStream in) {
        return create(in, System.out);
    }

    /**
     * Creates a new command handler for the specified input stream
     * and output stream
     *
     * @param in  The input stream to fetch data from.
     * @param out The output stream to send messages to.
     * @return The newly created command handler.
     */
    static ConsoleCommandHandler create(@NotNull InputStream in, @NotNull PrintStream out) {
        n(in, "InputStream cannot be null!");
        n(in, "PrintStream cannot be null!");
        Scanner scanner = new Scanner(in);
        return new CLIHandler(scanner, out);
    }

}
