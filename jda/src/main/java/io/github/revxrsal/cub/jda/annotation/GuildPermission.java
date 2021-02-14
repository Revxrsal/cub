package io.github.revxrsal.cub.jda.annotation;

import net.dv8tion.jda.api.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation to make a command executable only by users who have
 * certain Discord guild permissions, such as {@link Permission#ADMINISTRATOR}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface GuildPermission {

    /**
     * The permissions required to use the command
     *
     * @return The permissions
     */
    Permission[] value();

}

