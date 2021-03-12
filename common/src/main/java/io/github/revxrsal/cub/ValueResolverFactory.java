package io.github.revxrsal.cub;

import io.github.revxrsal.cub.ParameterResolver.ValueResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Creates a {@link ValueResolver} for specific types of parameters. These are
 * most useful in the following cases:
 * <ul>
 *     <li>Creating a value resolver for only a specific type of parameters,
 *     for example those with a specific annotation</li>
 *     <li>Creating value resolvers for a common interface or class</li>
 * </ul>
 * <p>
 * Example: We want to create a resolver that finds values for enums
 * <pre>{@code
 *
 * public class EnumValueFactory implements ValueResolverFactory {
 *
 *     public ValueResolver<?> create(CommandParameter parameter, HandledCommand command, CommandHandler handler) {
 *         if (!parameter.getType().isEnum()) return null;
 *         Class<? extends Enum> type = (Class<? extends Enum>) parameter.getType();
 *         return new ValueResolver<Enum>() {
 *             @Override public Enum resolve(ArgumentStack args, CommandSubject subject, CommandParameter parameter1) throws Throwable {
 *                 String value = args.popForParameter(parameter);
 *                 try {
 *                     return Enum.valueOf(type, args.pop());
 *                 } catch (IllegalArgumentException e) {
 *                     throw new ResolverFailedException(this, value, parameter1, e);
 *                 }
 *             }
 *         };
 *    }
 * }</pre>
 * <p>
 * Note that {@link ValueResolverFactory}ies must be registered
 * with {@link CommandHandler#registerValueResolverFactory(ValueResolverFactory)}.
 */
public interface ValueResolverFactory extends ResolverFactory<ValueResolver<?>> {

    /**
     * Creates a value resolver for the specified type, or {@code null} if this type
     * is not supported by this factory.
     *
     * @param parameter The parameter to create for
     * @param command   The declaring command
     * @param handler   The command handler
     * @return The {@link ValueResolver}, or null if not supported.
     */
    @Nullable ValueResolver<?> create(@NotNull CommandParameter parameter,
                                      @NotNull HandledCommand command,
                                      @NotNull CommandHandler handler);

    /**
     * Creates a {@link ValueResolverFactory} that will return the same
     * resolver for all parameters that match a specific type
     *
     * @param type     Type to check for
     * @param resolver The value resolver to use
     * @param <T>      The resolver value type
     * @return The resolver factory
     */
    static <T> @NotNull ValueResolverFactory forType(Class<T> type, ValueResolver<T> resolver) {
        return (parameter, command, handler) -> parameter.getType() == type ? resolver : null;
    }

    /**
     * Creates a {@link ValueResolverFactory} that will return the same
     * resolver for all parameters that match or extend a specific type
     *
     * @param type     Type to check for
     * @param resolver The value resolver to use
     * @param <T>      The resolver value type
     * @return The resolver factory
     */
    static <T> @NotNull ValueResolverFactory forHierarchyType(Class<T> type, ValueResolver<T> resolver) {
        return (parameter, command, handler) -> parameter.getType() == type
                || parameter.getType().isAssignableFrom(type) ? resolver : null;
    }

}
