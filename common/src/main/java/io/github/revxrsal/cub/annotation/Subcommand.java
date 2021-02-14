package io.github.revxrsal.cub.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a subcommand inside a parent {@link Command}. Subcommands can be registered in
 * inner classes whose parents are a {@link Command}.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Subcommand {

    /**
     * The subcommand name
     *
     * @return The subcommand name
     */
    String value();

    /**
     * The subcommand aliases, if any
     *
     * @return The subcommand aliases
     */
    String[] aliases() default {};

}
