package io.github.revxrsal.cub.bukkit;

import io.github.revxrsal.cub.CommandSubject;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a Bukkit command sender
 */
public interface BukkitCommandSubject extends CommandSubject {

    /**
     * The underlying {@link CommandSender} for this subject
     *
     * @return The underlying sender
     */
    CommandSender getSender();

    /**
     * Returns whether is this sender a player or not
     *
     * @return Whether is that sender a player or not
     */
    boolean isPlayer();

    /**
     * Returns this sender as a {@link Player}.
     *
     * @return This sender as a player. Returns null if not a player.
     */
    @Nullable Player asPlayer();

    /**
     * Requires this sender to be player, otherwise throws {@link SenderNotPlayerException}.
     *
     * @return The sender as the player
     */
    @NotNull Player requirePlayer();

}
