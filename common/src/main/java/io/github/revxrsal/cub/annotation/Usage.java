package io.github.revxrsal.cub.annotation;

import io.github.revxrsal.cub.HandledCommand;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to give the {@link HandledCommand} a usage.
 * <p>
 * If not present, it will be auto-generated
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Usage {

    /**
     * The command usage
     *
     * @return The usage
     */
    String value();

}
