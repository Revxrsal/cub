package io.github.revxrsal.cub.bukkit.core;

import io.github.revxrsal.cub.CommandContext;
import io.github.revxrsal.cub.bukkit.SenderNotPlayerException;
import io.github.revxrsal.cub.core.BaseCommandHandler;
import io.github.revxrsal.cub.core.BaseDispatcher;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class BukkitDispatcher extends BaseDispatcher implements CommandExecutor {

    public BukkitDispatcher(BaseCommandHandler handler) {
        super(handler);
    }

    @Override protected boolean isPossibleSender(@NotNull Class<?> v) {
        return Player.class.isAssignableFrom(v) || CommandSender.class.isAssignableFrom(v);
    }

    @Override protected Object handlePossibleSender(@NotNull Class<?> type, @NotNull CommandContext commandContext) {
        BukkitContextImpl context = (BukkitContextImpl) commandContext;
        if (Player.class.isAssignableFrom(type))
            if (!(context.getSubject().isPlayer())) {
                throw new SenderNotPlayerException();
            }
        return context.getSubject().getSender();
    }

    @Override public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        args = (String[]) ArrayUtils.add(args, 0, command.getName());
        BukkitSubject subject = new BukkitSubject(sender);
        BukkitContextImpl context = new BukkitContextImpl(subject, command);
        execute(subject, context, args);
        return false;
    }
}
