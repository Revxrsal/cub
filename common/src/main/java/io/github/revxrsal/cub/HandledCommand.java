package io.github.revxrsal.cub;

import io.github.revxrsal.cub.annotation.Conditions;
import io.github.revxrsal.cub.annotation.Description;
import io.github.revxrsal.cub.annotation.PrivateCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Represents a registered command
 */
public interface HandledCommand  {

    /**
     * Returns the command name provided by annotations
     *
     * @return The command name
     */
    String getName();

    /**
     * Returns the command description. Can be null if no {@link Description}
     * is set.
     *
     * @return The description
     */
    @Nullable
    String getDescription();

    /**
     * Returns the command usage.
     *
     * @return The usage
     */
    @NotNull
    String getUsage();

    /**
     * Returns the command aliases.
     *
     * @return The command aliases
     */
    @NotNull @Unmodifiable List<String> getAliases();

    /**
     * Returns the parent of this command. Can be null if this command is a root command.
     *
     * @return The parent of this command.
     * @see #isRootCommand()
     */
    @Nullable HandledCommand getParent();

    /**
     * Returns the permission required to execute this command. Each platform
     * has its own annotations to define this.
     * <p>
     * Note that, for commands that do not define permission annotations,
     * this will return a static instance of {@link CommandPermission} whose
     * method {@link CommandPermission#canExecute(CommandSubject)} always returns true. But
     * this method will never return null.
     *
     * @return The permission required to execute this command.
     */
    @NotNull CommandPermission getPermission();

    /**
     * Whether is this command a root command. This implements to
     * <br><code>getParent() != null</code>
     *
     * @return Whether is this command a root one
     */
    boolean isRootCommand();

    /**
     * Returns the parameters of this command. If this command is a category
     * command, then this will be empty.
     *
     * @return The command parameters
     */
    @NotNull @Unmodifiable List<CommandParameter> getParameters();

    /**
     * Returns the conditions that are evaluated before invoking this
     * command. Declared by {@link Conditions}.
     *
     * @return The command conditions
     */
    @NotNull List<CommandCondition> getConditions();

    /**
     * Returns the subcommands of this command. This can be empty.
     *
     * @return The subcommands
     */
    @NotNull @Unmodifiable Map<String, HandledCommand> getSubcommands();

    /**
     * Whether is this command executed asynchronously or not.
     *
     * @return Whether is this command async or not
     */
    boolean isAsync();

    /**
     * Whether is this command annotated with {@link PrivateCommand} or not
     *
     * @return Whether is this command private or not
     */
    boolean isPrivate();

    /**
     * Returns the annotation with the specified value.
     *
     * @param annotation The annotation type
     * @param <A>        The annotation type
     * @return The annotation value, or null if not present.
     */
    <A extends Annotation> A getAnnotation(@NotNull Class<A> annotation);

    /**
     * Returns whether does this command have the specified annotation
     *
     * @param annotation The annotation type
     * @return true if the annotation is present, false if otherwise.
     */
    boolean hasAnnotation(@NotNull Class<? extends Annotation> annotation);

    /**
     * Returns the command handler that instantiated this command
     *
     * @return The owning command handler
     */
    @NotNull
    CommandHandler getCommandHandler();

    /**
     * Returns the thread {@link Executor} that is used to invoke this command.
     * A direct, synchronous executor will be used if {@link #isAsync()} is false.
     *
     * @return The executor used by this command
     */
    @NotNull Executor getExecutor();

}
