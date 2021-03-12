package io.github.revxrsal.cub;

import io.github.revxrsal.cub.annotation.*;
import io.github.revxrsal.cub.exception.CommandException;
import io.github.revxrsal.cub.exception.CommandExceptionHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The main handler for registering commands, resolvers, interceptors, handlers,
 * tab completions and other stuff.
 * <p>
 * Construct instances from subclasses:
 * <ul>
 *     <li>BukkitCommandHandler</li>
 *     <li>JDACommandHandler</li>
 * </ul>
 */
public interface CommandHandler {

    /**
     * Registers the specified command from an instance. This will automatically
     * set all {@link Dependency}-annotated fields with their values.
     *
     * @param command The command object instance. Can be a class if methods are static.
     * @return This command handler
     */
    CommandHandler registerCommand(@NotNull Object... command);

    /**
     * Registers the specified instance in which includes any methods annotated
     * with the following:
     * <ul>
     *     <li>{@link ContextResolver}</li>
     *     <li>{@link ValueResolver}</li>
     *     <li>{@link ConditionEvaluator}</li>
     *     <li>Any platform-impl-specified resolver annotations.</li>
     * </ul>
     * See the aforementioned annotations' documentation for more details
     *
     * @param resolvers The resolver objects (or classes)
     * @return This command handler
     */
    CommandHandler registerResolvers(@NotNull Object... resolvers);

    /**
     * Registers the specified condition, in which adds pre-invocation checks
     * to the command.
     *
     * @param conditionID The condition id
     * @param condition   The condition
     * @return This command handler
     */
    CommandHandler registerCondition(@NotNull String conditionID, @NotNull CommandCondition condition);

    /**
     * Registers the specified condition in which all commands will be
     * validated with.
     *
     * @param condition Condition to add
     * @return This command handler
     */
    CommandHandler registerGlobalCondition(@NotNull CommandCondition condition);

    /**
     * Registers a {@link ParameterValidator} for the specified parameter type. Parameter
     * validators can access all information about a parameter, including the name and annotations.
     *
     * @param parameterType The parameter type
     * @param validator     The validator for this parameter
     * @param <T>           The parameter type
     * @return This command handler
     */
    <T> CommandHandler registerParameterValidator(@NotNull Class<T> parameterType, @NotNull ParameterValidator<T> validator);

    /**
     * Registers a parameter resolver that gets its value from the command arguments.
     * <p>
     * See {@link ParameterResolver.ValueResolver} for more information
     *
     * @param type     The parameter type to resolve
     * @param resolver The resolver
     * @return This command handler
     * @see io.github.revxrsal.cub.ParameterResolver.ValueResolver
     */
    <T> CommandHandler registerTypeResolver(@NotNull Class<T> type, @NotNull ParameterResolver.ValueResolver<T> resolver);

    /**
     * Registers a parameter resolver that gets its value from the command context.
     * <p>
     * See {@link ParameterResolver.ContextResolver} for more information
     *
     * @param type     The parameter type to resolve
     * @param resolver The resolver
     * @return This command handler
     * @see io.github.revxrsal.cub.ParameterResolver.ContextResolver
     */
    <T> CommandHandler registerContextResolver(@NotNull Class<T> type, @NotNull ParameterResolver.ContextResolver<T> resolver);

    /**
     * Registers a dependency for dependency injection.
     * <p>
     * Any fields in the command class or instance with the {@link Dependency} annotation
     * will have their value set from this supplier.
     *
     * @param dependencyType The dependency class type. This <i>must</i> match
     *                       the field type.
     * @param supplier       The dependency supplier
     * @param <T>            The dependency type
     * @return This command handler
     * @see #registerDependency(Class, Object)
     */
    <T> CommandHandler registerDependency(@NotNull Class<T> dependencyType, Supplier<T> supplier);

    /**
     * Registers a (static) dependency for dependency injection.
     * <p>
     * Any fields in the command class or instance with the {@link Dependency} annotation
     * will have their value set to this value.
     *
     * @param dependencyType The dependency class type. This <i>must</i> match
     *                       the field type
     * @param value          The dependency value
     * @param <T>            The dependency type
     * @return This command handler
     * @see #registerDependency(Class, Supplier)
     */
    <T> CommandHandler registerDependency(@NotNull Class<T> dependencyType, T value);

    /**
     * Registers a response handler for the specified response type. Response handlers
     * do post-handling with results returned from command methods.
     * <p>
     * Note that response handlers are captured by {@link HandledCommand}s when they are
     * registered, so they should be registered <i>before</i> the command itself is
     * registered.
     *
     * @param responseType    The response class
     * @param responseHandler The response handler implementation
     * @param <T>             The response type
     * @return This command handler
     */
    <T> CommandHandler registerResponseHandler(@NotNull Class<T> responseType, @NotNull ResponseHandler<T> responseHandler);

    /**
     * Returns all the registered commands of this command handler.
     * <p>
     * Note that this is an <i>unmodifiable view</i>. You cannot modify this
     * directly.
     *
     * @return All commands registered on this handler
     */
    @NotNull @UnmodifiableView Map<String, HandledCommand> getCommands();

    /**
     * Returns the command exception handler currently used by this command handler
     *
     * @return The command exception handler
     */
    @NotNull CommandExceptionHandler getExceptionHandler();

    /**
     * Sets the {@link CommandExceptionHandler} to use for handling any exceptions
     * that are thrown from the command.
     * <p>
     * If not set, a default one will be used.
     *
     * @param exceptionHandler The exception handler
     * @return This command handler
     * @see CommandExceptionHandler#handleException(CommandSubject, CommandHandler, HandledCommand, List, CommandContext, Throwable, boolean)
     * @see CommandException
     */
    CommandHandler setExceptionHandler(@NotNull CommandExceptionHandler exceptionHandler);

    /**
     * Returns the prefix that comes before all {@link Switch} parameters
     * when they are fetched from the command.
     *
     * @return The switch prefix
     */
    @NotNull String getSwitchPrefix();

    /**
     * Sets the prefix that all parameters annotated with {@link Switch} will
     * be checked against. If not set, <blockquote>-</blockquote> will be used
     *
     * @param prefix New prefix to set
     * @return This command handler
     * @throws NullPointerException     if the prefix is null
     * @throws IllegalArgumentException if the prefix is empty
     */
    CommandHandler setSwitchPrefix(@NotNull String prefix);

    /**
     * Returns the prefix that comes before all {@link Flag} parameters
     * when they are fetched from the command.
     *
     * @return The switch prefix
     */
    @NotNull String getFlagPrefix();

    /**
     * Sets the prefix that all parameters annotated with {@link Flag} will
     * be checked against. If not set, <blockquote>-</blockquote> will be used
     *
     * @param prefix New prefix to set
     * @return This command handler
     * @throws NullPointerException     if the prefix is null
     * @throws IllegalArgumentException if the prefix is empty
     */
    CommandHandler setFlagPrefix(@NotNull String prefix);

    /**
     * Returns the {@link CommandHelpWriter} used to generate the
     * appropriate help entries. If not set, this will return null.
     * <p>
     * See {@link CommandHelpWriter} for more information.
     *
     * @param <T> The command help writer entry type
     * @return The writer
     * @see CommandHelp
     * @see CommandHelpWriter
     * @since 1.7.0
     */
    <T> CommandHelpWriter<T> getHelpWriter();

    /**
     * Sets the {@link CommandHelpWriter} used to generate the
     * appropriate help entries.
     * <p>
     * See {@link CommandHelpWriter} for more information.
     *
     * @param writer New writer to use.
     * @param <T>    The command help writer entry type
     * @return The writer
     * @see CommandHelp
     * @see CommandHelpWriter
     * @since 1.7.0
     */
    <T> CommandHandler setHelpWriter(@NotNull CommandHelpWriter<T> writer);

    /**
     * Registers a {@link ValueResolverFactory} to this handler
     *
     * @param factory Factory to register
     * @return This command handler
     * @see ValueResolverFactory
     * @see #registerContextResolverFactory(ContextResolverFactory)
     * @since 1.8.0
     */
    CommandHandler registerValueResolverFactory(@NotNull ValueResolverFactory factory);

    /**
     * Registers a {@link ValueResolverFactory} to this handler
     *
     * @param factory Factory to register
     * @return This command handler
     * @see ContextResolverFactory
     * @see #registerValueResolverFactory(ValueResolverFactory)
     * @since 1.8.0
     */
    CommandHandler registerContextResolverFactory(@NotNull ContextResolverFactory factory);

}
