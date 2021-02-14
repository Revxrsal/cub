package io.github.revxrsal.cub;

import io.github.revxrsal.cub.annotation.ConditionEvaluator;
import io.github.revxrsal.cub.exception.CommandExceptionHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a condition that must be met in order for the command
 * invocation to continue.
 * <p>
 * You can register command conditions in multiple ways:
 * <ul>
 *     <li>{@link CommandHandler#registerCondition(String, CommandCondition)}</li>
 *     <li>{@link CommandHandler#registerGlobalCondition(CommandCondition)}</li>
 *     <li>{@link CommandHandler#registerResolvers(Object...)} with a method annotated with {@link ConditionEvaluator}.</li>
 * </ul>
 */
public interface CommandCondition {

    /**
     * Evaluates the condition.
     * <p>
     * Ideally, this should throw any exceptions if the condition fails, and lets
     * them get handled by the {@link CommandExceptionHandler}.
     *
     * @param subject The command subject
     * @param args    The command arguments
     * @param command The invoked command
     * @param context The command invocation context
     */
    void test(@NotNull CommandSubject subject,
              @NotNull List<String> args,
              @NotNull HandledCommand command,
              @NotNull CommandContext context) throws Throwable;

}
