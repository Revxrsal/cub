package io.github.revxrsal.cub.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation used to validate any {@link Number} argument types (primitives included).
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Range {

    /**
     * The minimum value allowed
     *
     * @return The minimum value
     */
    double min() default Double.MIN_VALUE;

    /**
     * The maximum value allowed
     *
     * @return The maximum value
     */
    double max() default Double.MAX_VALUE;

}
