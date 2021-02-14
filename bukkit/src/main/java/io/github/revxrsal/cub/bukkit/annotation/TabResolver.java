package io.github.revxrsal.cub.bukkit.annotation;

import io.github.revxrsal.cub.CommandHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for tab completion resolvers; methods that take one (or more) of the following parameters:
 * <ul>
 *     <li>{@link org.bukkit.command.CommandSender}</li>
 *     <li>{@link org.bukkit.entity.Player} as the sender or null if not a player</li>
 *     <li>{@link io.github.revxrsal.cub.bukkit.BukkitCommandSubject}</li>
 *     <li>{@link java.util.List} of strings representing the arguments</li>
 *     <li>{@link org.bukkit.command.Command}</li>
 *     <li>{@link io.github.revxrsal.cub.HandledCommand}</li>
 * </ul> in no specific order, and return the appropriate result.
 * <p>
 * Register with {@link CommandHandler#registerResolvers(Object...)}
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TabResolver {

    /**
     * The tab suggestion ID
     *
     * @return The suggestion ID
     */
    String value();

}
