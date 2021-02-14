package io.github.revxrsal.cub.bukkit.annotation;

import io.github.revxrsal.cub.CommandHandler;
import io.github.revxrsal.cub.bukkit.TabSuggestionProvider;
import io.github.revxrsal.cub.bukkit.core.BukkitHandler;
import org.intellij.lang.annotations.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adds tab completion for the command.
 * <p>
 * Each value in {@link #value()} should be separated with a space, and can either be
 * contextual or static:
 * <ul>
 *     <li>If it is contextual, it should be prefixed with <code>@</code> and
 *     registered with {@link BukkitHandler#registerTabSuggestion(String, TabSuggestionProvider)}
 *     or {@link TabResolver} and {@link CommandHandler#registerResolvers(Object...)}.</li>
 *     <li>
 *         If it is static, you can either write up the values right away and separate them
 *         with <code>|</code>, such as <em>1|2|3</em> which will return 1, 2 and 3 when
 *         tab is requested.
 *     </li>
 * </ul>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TabCompletion {

    /**
     * The tab completion value, by order.
     *
     * @return The tab completion. Check the class documentation
     * for more information.
     */
    @Pattern("@?([\\w ]+)\\|?")
    String value();

}
