package io.github.revxrsal.cub.bungee.core;

import io.github.revxrsal.cub.ArgumentStack;
import io.github.revxrsal.cub.CommandParameter;
import io.github.revxrsal.cub.CommandSubject;
import io.github.revxrsal.cub.ParameterResolver.ValueResolver;
import io.github.revxrsal.cub.bungee.BungeeCommandSubject;
import io.github.revxrsal.cub.bungee.PlayerSelector;
import io.github.revxrsal.cub.exception.InvalidValueException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

final class SelectorParam implements ValueResolver<PlayerSelector> {

    public static final SelectorParam INSTANCE = new SelectorParam();

    @Override public PlayerSelector resolve(@NotNull ArgumentStack args, @NotNull CommandSubject commandSubject, @NotNull CommandParameter parameter) throws Throwable {
        BungeeCommandSubject subject = (BungeeCommandSubject) commandSubject;
        String value = args.pop().toLowerCase();
        List<ProxiedPlayer> coll = new ArrayList<>();
        ProxiedPlayer[] players = ProxyServer.getInstance().getPlayers().toArray(new ProxiedPlayer[0]);
        switch (value) {
            case "@r":
                coll.add(players[ThreadLocalRandom.current().nextInt(players.length)]);
                return coll::iterator;
            case "@a": {
                Collections.addAll(coll, players);
                return coll::iterator;
            }
            case "@s":
            case "@p": {
                coll.add(subject.requirePlayer());
                return coll::iterator;
            }
            default: {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(value);
                if (player == null)
                    throw new InvalidValueException(InvalidValueException.PLAYER, value);
                coll.add(player);
                return coll::iterator;
            }
        }
    }
}
