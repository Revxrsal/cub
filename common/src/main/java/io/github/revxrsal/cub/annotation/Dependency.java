package io.github.revxrsal.cub.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Supplier;

/**
 * An annotation for fields used to inject dependencies into command classes.
 *
 * @see io.github.revxrsal.cub.CommandHandler#registerDependency(Class, Object)
 * @see io.github.revxrsal.cub.CommandHandler#registerDependency(Class, Supplier)
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dependency {

}
