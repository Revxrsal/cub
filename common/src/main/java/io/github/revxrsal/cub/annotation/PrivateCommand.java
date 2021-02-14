package io.github.revxrsal.cub.annotation;

import io.github.revxrsal.cub.HandledCommand;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a command as private and should not appear in help menus or tab completions.
 * <p>
 * Accessible through {@link HandledCommand#isPrivate()}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface PrivateCommand {

}
