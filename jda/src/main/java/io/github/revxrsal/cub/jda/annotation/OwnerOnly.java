package io.github.revxrsal.cub.jda.annotation;

import net.dv8tion.jda.api.entities.Member;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a command as only usable by the guild owner.
 * <p>
 * Uses {@link Member#isOwner()} to validate.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface OwnerOnly {

}
