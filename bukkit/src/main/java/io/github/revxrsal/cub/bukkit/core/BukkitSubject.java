package io.github.revxrsal.cub.bukkit.core;

import io.github.revxrsal.cub.bukkit.BukkitCommandSubject;
import io.github.revxrsal.cub.bukkit.SenderNotPlayerException;
import io.github.revxrsal.cub.core.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

class BukkitSubject implements BukkitCommandSubject {

    private static final UUID CONSOLE_UUID = new UUID(0, 0);

    private final CommandSender sender;

    public BukkitSubject(CommandSender sender) {
        this.sender = sender;
    }

    @Override public @NotNull String getName() {
        return sender.getName();
    }

    @Override public void reply(@NotNull String message) {
        sender.sendMessage(Utils.colorize(message));
    }

    public CommandSender getSender() {
        return sender;
    }

    @Override public boolean isPlayer() {
        return sender instanceof Player;
    }

    @Override public @Nullable Player asPlayer() {
        return sender instanceof Player ? (Player) sender : null;
    }

    @Override public @NotNull Player requirePlayer() throws SenderNotPlayerException {
        if (!(sender instanceof Player))
            throw new SenderNotPlayerException();
        return (Player) sender;
    }

    @Override public @NotNull UUID getUUID() {
        return sender instanceof ConsoleCommandSender ? CONSOLE_UUID : ((Player) sender).getUniqueId();
    }
}
