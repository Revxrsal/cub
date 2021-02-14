package io.github.revxrsal.cub.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for root commands.
 * <p>
 * These commands come directly after the slash.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Command {

    /**
     * The command main name
     *
     * @return The command name
     */
    String value();

    /**
     * The command aliases, if any.
     *
     * @return The command aliases
     */
    String[] aliases() default {};

}
