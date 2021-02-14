package io.github.revxrsal.cub.jda.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation to make a command only available for execution by
 * a set of roles.
 * <p>
 * You can either define the role IDs by {@link #ids()} or the role names by {@link #names()}.
 * Defining both will simply check for IDs then check for names.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RolePermission {

    /**
     * The roles IDs in which they can execute the command.
     *
     * @return The roles IDs.
     */
    long[] ids() default {};

    /**
     * The roles names in which they can execute the command
     *
     * @return The roles names.
     */
    String[] names() default {};

}
