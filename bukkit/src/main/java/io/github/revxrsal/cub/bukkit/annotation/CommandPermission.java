package io.github.revxrsal.cub.bukkit.annotation;

import org.bukkit.permissions.PermissionDefault;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation to indicate that a permission is required in order to invoke
 * this command.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface CommandPermission {

    /**
     * The permission node
     *
     * @return The permission node
     */
    String value();

    /**
     * The permission default access (those who do not need this permission explicitly added
     * to them). By default, only operators do.
     *
     * @return The permission default access
     */
    PermissionDefault access() default PermissionDefault.OP;

}
