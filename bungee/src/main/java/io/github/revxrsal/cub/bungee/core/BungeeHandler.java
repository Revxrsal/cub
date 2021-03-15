package io.github.revxrsal.cub.bungee.core;

import com.google.common.collect.ImmutableList;
import io.github.revxrsal.cub.CommandContext;
import io.github.revxrsal.cub.CommandHandler;
import io.github.revxrsal.cub.CommandSubject;
import io.github.revxrsal.cub.HandledCommand;
import io.github.revxrsal.cub.ParameterResolver.ContextResolver;
import io.github.revxrsal.cub.bungee.BungeeCommandHandler;
import io.github.revxrsal.cub.bungee.BungeeCommandSubject;
import io.github.revxrsal.cub.bungee.PlayerSelector;
import io.github.revxrsal.cub.bungee.TabSuggestionProvider;
import io.github.revxrsal.cub.bungee.annotation.TabResolver;
import io.github.revxrsal.cub.core.BaseCommandHandler;
import io.github.revxrsal.cub.exception.InvalidValueException;
import lombok.SneakyThrows;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.*;

import static io.github.revxrsal.cub.core.Utils.*;

@Internal
public final class BungeeHandler extends BaseCommandHandler implements BungeeCommandHandler {

    final Plugin plugin;

    final Map<String, TabSuggestionProvider> tab = new HashMap<>();
    final Map<Class<?>, TabSuggestionProvider> tabByParam = new HashMap<>();

    public BungeeHandler(Plugin plugin) {
        super();
        this.plugin = plugin;
        registerDependency((Class) plugin.getClass(), plugin);
        registerDependency(Plugin.class, plugin);
        registerTypeResolver(ProxiedPlayer.class, (args, subject, parameter) -> {
            String name = args.pop();
            if (name.equalsIgnoreCase("me")) return ((BungeeSubject) subject).requirePlayer();
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(name);
            if (player == null) throw new InvalidValueException(InvalidValueException.PLAYER, name);
            return player;
        });
        registerTypeResolver(PlayerSelector.class, SelectorParam.INSTANCE);
        registerStaticTabSuggestion("nothing", Collections.emptyList());
        registerTabSuggestion("selectors", (args, sender, command, bungeeCommand) -> {
            List<String> completions = new ArrayList<>();
            completions.add("@a");
            completions.add("@r");
            completions.add("@p");
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers())
                completions.add(player.getName());
            return completions;
        });
        registerParameterTab(PlayerSelector.class, "selectors");
        registerTabSuggestion("players", (args, sender, command, bungeeCommand) -> BungeeCmd.playerList(args.get(args.size() - 1)));
        registerParameterTab(ProxiedPlayer.class, "players");
        registerContextResolver(plugin.getClass(), (ContextResolver) ContextResolver.of(plugin));
        registerContextResolver(BungeeCommandSubject.class, (args, sender, parameter) -> (BungeeCommandSubject) sender);
        registerContextResolver(CommandSender.class, (args, sender, parameter) -> ((BungeeCommandSubject) sender).getSender());
        registerContextResolver(ProxyServer.class, ContextResolver.of(ProxyServer::getInstance));
        registerResponseHandler(BaseComponent.class, (response, subject, command, context) -> ((BungeeSubject) subject).getSender().sendMessage(response));
        setExceptionHandler(DefaultExceptionHandler.INSTANCE);
    }

    @SneakyThrows
    protected void addResolvers(Method method, Object resolver) {
        TabResolver ce = method.getAnnotation(TabResolver.class);
        if (ce != null) {
            ensureAccessible(method);
            MethodHandle handle = bind(MethodHandles.lookup().unreflect(method), resolver);
            Class<?>[] ptypes = method.getParameterTypes();
            registerTabSuggestion(ce.value(), (args, sender, command, bcmd) -> {
                List<Object> ia = new ArrayList<>();
                for (Class<?> ptype : ptypes) {
                    if (List.class.isAssignableFrom(ptype)) {
                        ia.add(args);
                    } else if (CommandSubject.class.isAssignableFrom(ptype)) {
                        ia.add(sender);
                    } else if (HandledCommand.class.isAssignableFrom(ptype)) {
                        ia.add(command);
                    } else if (Command.class.isAssignableFrom(ptype)) {
                        ia.add(bcmd);
                    } else if (CommandSender.class.isAssignableFrom(ptype)) {
                        ia.add(sender.getSender());
                    } else if (ProxiedPlayer.class.isAssignableFrom(ptype)) {
                        ia.add(sender.requirePlayer());
                    }
                }
                return (Collection<String>) handle.invokeWithArguments(ia);
            });
        }
    }

    @Override public CommandHandler registerCommand(@NotNull Object... instances) {
        for (Object instance : instances) {
            BungeeCommand command = new BungeeCommand(plugin, this, instance, null, null);
            if (command.getName() != null) addCommand(command);
            setDependencies(instance);
        }
        return this;
    }

    public BungeeCommandHandler registerTabSuggestion(@NotNull String suggestionID, @NotNull TabSuggestionProvider provider) {
        tab.put(n(suggestionID, "id"), n(provider, "provider"));
        return this;
    }

    public BungeeCommandHandler registerStaticTabSuggestion(@NotNull String suggestionID, @NotNull Collection<String> completions) {
        ImmutableList<String> values = ImmutableList.copyOf(completions);
        tab.put(n(suggestionID, "id"), (args, sender, command, bungeeCommand) -> values);
        return this;
    }

    public BungeeCommandHandler registerStaticTabSuggestion(@NotNull String suggestionID, @NotNull String... completions) {
        ImmutableList<String> values = ImmutableList.copyOf(completions);
        tab.put(n(suggestionID, "id"), (args, sender, command, bungeeCommand) -> values);
        return this;
    }

    @Override public BungeeCommandHandler registerParameterTab(@NotNull Class<?> parameterType, @NotNull TabSuggestionProvider provider) {
        tabByParam.put(n(parameterType, "parameterType"), n(provider, "provider"));
        return this;
    }

    @Override public BungeeCommandHandler registerParameterTab(@NotNull Class<?> parameterType, @NotNull String providerID) {
        tabByParam.put(n(parameterType, "parameterType"), c(tab.get(providerID), "No such suggestion provider: " + providerID));
        return this;
    }

    public TabSuggestionProvider getTabs(@NotNull Class<?> type) {
        TabSuggestionProvider provider = tabByParam.getOrDefault(type, TabSuggestionProvider.EMPTY);
        if (provider == TabSuggestionProvider.EMPTY && type.isEnum()) {
            List<String> completions = BungeeCmd.enums(type);
            tabByParam.put(type, provider = (args, sender, command, bungeeCommand) -> completions);
        }
        return provider;
    }

    @Override protected void injectValues(Class<?> type, @NotNull CommandSubject sender, @NotNull List<String> args, @NotNull HandledCommand command, @NotNull CommandContext bcmd, List<Object> ia) {
        if (CommandSender.class.isAssignableFrom(type))
            ia.add(((BungeeSubject) sender).getSender());
    }

    @Override public @NotNull Plugin getPlugin() {
        return plugin;
    }
}
