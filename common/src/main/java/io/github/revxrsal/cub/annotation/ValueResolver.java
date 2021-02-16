package io.github.revxrsal.cub.annotation;

import io.github.revxrsal.cub.ArgumentStack;
import io.github.revxrsal.cub.CommandHandler;
import io.github.revxrsal.cub.CommandParameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for context resolvers; methods that take one (or more) of the following parameters:
 * <ul>
 *     <li>{@link io.github.revxrsal.cub.CommandSubject}</li>
 *     <li>{@link ArgumentStack}</li>
 *     <li>{@link io.github.revxrsal.cub.CommandParameter}</li>
 *     <li>{@link java.util.List} of strings as the arguments (immutable)</li>
 *     <li>{@link String} fetched from {@link ArgumentStack#popForParameter(CommandParameter)}.</li>
 * </ul> in no specific order, and return the appropriate result.
 * <p>
 * Register with {@link CommandHandler#registerResolvers(Object...)}
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueResolver {

    /**
     * The return result of this resolver
     *
     * @return The return type
     */
    Class<?> value();

}
