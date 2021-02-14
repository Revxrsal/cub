package io.github.revxrsal.cub.annotation;

import io.github.revxrsal.cub.HandledCommand;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to give the {@link HandledCommand} a syntax.
 * <p>
 * If not present, it will be auto-generated
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Syntax {

    /**
     * The command syntax
     *
     * @return The syntax
     */
    String value();

}
