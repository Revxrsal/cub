package io.github.revxrsal.cub.annotation;

import io.github.revxrsal.cub.CommandParameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adds a default value for a parameter when it is not supplied.
 * <p>
 * Due to limitations and simply the lots of "edge cases", this parameter only works if
 * there are no parameters after it, or if all following parameters are also
 * marked with {@link Default}.
 * <p>
 * Accessible with {@link CommandParameter#getDefaultValue()}.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Default {

    /**
     * The default value. This will be passed to resolvers just as if the user inputted it.
     * <p>
     * If none is specified, and the argument is not present, {@code null}
     * will be passed to the command.
     *
     * @return The parameter default value.
     */
    String value();

}
