package io.github.revxrsal.cub.annotation;

import io.github.revxrsal.cub.CommandCondition;
import io.github.revxrsal.cub.CommandHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the conditions that must be met before this command is
 * executed.
 * <p>
 * Conditions can be registered with {@link CommandHandler#registerCondition(String, CommandCondition)},
 * or using {@link ConditionEvaluator}.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Conditions {

    /**
     * The condition IDs to check against
     *
     * @return The condition IDs
     */
    String[] value();

}
