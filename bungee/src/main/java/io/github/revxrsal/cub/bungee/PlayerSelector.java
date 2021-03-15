package io.github.revxrsal.cub.bungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * A parameter that allows player selectors such as '@a', '@p', '@s', '@r'
 * or player names individually.
 * <p>
 * Simply iterate over the parameter value.
 */
public interface PlayerSelector extends Iterable<ProxiedPlayer> {

}
