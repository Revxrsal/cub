package io.github.revxrsal.cub.annotation;

import io.github.revxrsal.cub.CommandHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for condition evaluators/resolvers; methods that take one (or more) of the following parameters:
 * <ul>
 *     <li>{@link io.github.revxrsal.cub.CommandSubject}</li>
 *     <li>{@link com.google.common.collect.ImmutableList} of strings representing the arguments</li>
 *     <li>{@link io.github.revxrsal.cub.HandledCommand}</li>
 *     <li>{@link io.github.revxrsal.cub.CommandContext}</li>
 * </ul> in no specific order, and return the appropriate result.
 * <p>
 * Register with {@link CommandHandler#registerResolvers(Object...)}
 * <p>
 * Not to be confused with {@link Conditions}
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConditionEvaluator {

    /**
     * The condition ID
     *
     * @return The condition ID
     */
    String value();

}
