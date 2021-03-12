package io.github.revxrsal.cub;

import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A factory that constructs {@link ParameterResolver}s for specific type of parameters.
 *
 * @param <R> The parameter resolver type.
 *            <p>
 *            You should not implement this class! It exists for the sole purpose
 *            of providing a shared interface between subclasses.
 *            Only use {@link ValueResolverFactory} or {@link ContextResolverFactory}.
 * @see ValueResolverFactory
 * @see ContextResolverFactory
 */
@NonExtendable
public interface ResolverFactory<R extends ParameterResolver<?, ?>> {

    @Nullable R create(@NotNull CommandParameter parameter,
                       @NotNull HandledCommand command,
                       @NotNull CommandHandler handler);

}
