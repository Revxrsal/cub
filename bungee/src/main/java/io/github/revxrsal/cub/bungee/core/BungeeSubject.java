package io.github.revxrsal.cub.bungee.core;

import io.github.revxrsal.cub.bungee.BungeeCommandSubject;
import io.github.revxrsal.cub.bungee.SenderNotPlayerException;
import io.github.revxrsal.cub.core.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

final class BungeeSubject implements BungeeCommandSubject {

    private static final UUID CONSOLE_UUID = new UUID(0, 0);

    private final CommandSender sender;

    public BungeeSubject(CommandSender sender) {
        this.sender = sender;
    }

    @Override public CommandSender getSender() {
        return sender;
    }

    @Override public boolean isPlayer() {
        return sender instanceof ProxiedPlayer;
    }

    @Override public @Nullable ProxiedPlayer asPlayer() {
        return isPlayer() ? (ProxiedPlayer) sender : null;
    }

    @Override public @NotNull ProxiedPlayer requirePlayer() throws SenderNotPlayerException {
        if (!isPlayer())
            throw new SenderNotPlayerException();
        return (ProxiedPlayer) sender;
    }

    @Override public @NotNull String getName() {
        return sender.getName();
    }

    @Override public @NotNull UUID getUUID() {
        return isPlayer() ? requirePlayer().getUniqueId() : CONSOLE_UUID;
    }

    @Override public void reply(@NotNull String message) {
        sender.sendMessage(new TextComponent(Utils.colorize(message)));
    }
}
