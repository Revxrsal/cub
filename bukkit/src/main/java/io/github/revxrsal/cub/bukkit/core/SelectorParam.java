package io.github.revxrsal.cub.bukkit.core;

import io.github.revxrsal.cub.ArgumentStack;
import io.github.revxrsal.cub.CommandParameter;
import io.github.revxrsal.cub.CommandSubject;
import io.github.revxrsal.cub.ParameterResolver.ValueResolver;
import io.github.revxrsal.cub.bukkit.BukkitCommandSubject;
import io.github.revxrsal.cub.bukkit.PlayerSelector;
import io.github.revxrsal.cub.exception.InvalidValueException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

final class SelectorParam implements ValueResolver<PlayerSelector> {

    public static final SelectorParam INSTANCE = new SelectorParam();

    @Override public PlayerSelector resolve(@NotNull ArgumentStack args, @NotNull CommandSubject commandSubject, @NotNull CommandParameter parameter) throws Throwable {
        BukkitCommandSubject subject = (BukkitCommandSubject) commandSubject;
        String value = args.pop().toLowerCase();
        List<Player> coll = new ArrayList<>();
        Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);
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
                Player player = Bukkit.getPlayer(value);
                if (player == null)
                    throw new InvalidValueException(InvalidValueException.PLAYER, value);
                coll.add(player);
                return coll::iterator;
            }
        }
    }
}
