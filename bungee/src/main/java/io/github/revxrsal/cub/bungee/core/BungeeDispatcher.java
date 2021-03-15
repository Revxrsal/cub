package io.github.revxrsal.cub.bungee.core;

import io.github.revxrsal.cub.CommandContext;
import io.github.revxrsal.cub.bungee.SenderNotPlayerException;
import io.github.revxrsal.cub.core.BaseCommandHandler;
import io.github.revxrsal.cub.core.BaseDispatcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

class BungeeDispatcher extends BaseDispatcher {

    public BungeeDispatcher(BaseCommandHandler handler) {
        super(handler);
    }

    @Override public boolean isPossibleSender(@NotNull Class<?> v) {
        return isSender(v);
    }

    public static boolean isSender(@NotNull Class<?> v) {
        return ProxiedPlayer.class.isAssignableFrom(v) || CommandSender.class.isAssignableFrom(v);
    }

    @Override protected Object handlePossibleSender(@NotNull Class<?> type, @NotNull CommandContext commandContext) {
        BungeeContextImpl context = (BungeeContextImpl) commandContext;
        if (ProxiedPlayer.class.isAssignableFrom(type))
            if (!(context.getSubject().isPlayer())) {
                throw new SenderNotPlayerException();
            }
        return context.getSubject().getSender();
    }

}
