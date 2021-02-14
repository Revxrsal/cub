package io.github.revxrsal.cub.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation to catch invalid usages for a command. This annotation
 * can be added to methods to invoke code on any invalid usage.
 * <p>
 * Parameters on these methods can use any of these types, in no specific order:
 * <ul>
 *     <li>{@link io.github.revxrsal.cub.ArgumentStack}</li>
 *     <li>{@link io.github.revxrsal.cub.CommandHandler}</li>
 *     <li>{@link io.github.revxrsal.cub.HandledCommand}</li>
 *     <li>{@link io.github.revxrsal.cub.CommandContext}</li>
 *     <li>{@link io.github.revxrsal.cub.CommandSubject}</li>
 *     <li>{@link String}[]</li>
 *     <li>{@link java.util.List}&lt;{@link String}&gt;</li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CatchInvalid {

}
