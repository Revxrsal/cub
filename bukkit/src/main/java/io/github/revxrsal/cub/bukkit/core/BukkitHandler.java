package io.github.revxrsal.cub.bukkit.core;

import com.google.common.collect.ImmutableList;
import io.github.revxrsal.cub.CommandContext;
import io.github.revxrsal.cub.CommandHandler;
import io.github.revxrsal.cub.CommandSubject;
import io.github.revxrsal.cub.HandledCommand;
import io.github.revxrsal.cub.ParameterResolver.ContextResolver;
import io.github.revxrsal.cub.bukkit.BukkitCommandHandler;
import io.github.revxrsal.cub.bukkit.BukkitCommandSubject;
import io.github.revxrsal.cub.bukkit.PlayerSelector;
import io.github.revxrsal.cub.bukkit.TabSuggestionProvider;
import io.github.revxrsal.cub.bukkit.annotation.TabResolver;
import io.github.revxrsal.cub.core.BaseCommandHandler;
import io.github.revxrsal.cub.exception.InvalidValueException;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.rmi.server.ServerNotActiveException;
import java.util.*;

import static io.github.revxrsal.cub.core.Utils.*;

public final class BukkitHandler extends BaseCommandHandler implements BukkitCommandHandler {

    final Map<String, TabSuggestionProvider> tab = new HashMap<>();

    final Plugin plugin;

    public BukkitHandler(@NotNull Plugin plugin) {
        super();
        this.plugin = plugin;
        super.dependencies.put(plugin.getClass(), () -> plugin);
        registerTypeResolver(Player.class, (a, b, parameter) -> {
            String name = a.pop();
            if (name.equalsIgnoreCase("me")) return ((Player) b);
            Player player = Bukkit.getPlayer(name);
            if (player == null) throw new InvalidValueException(InvalidValueException.PLAYER, name);
            return player;
        });
        registerTypeResolver(OfflinePlayer.class, (a, b, parameter) -> {
            String name = a.pop();
            if (name.equalsIgnoreCase("me")) return ((Player) b);
            OfflinePlayer player = Bukkit.getOfflinePlayer(name);
            if (!player.hasPlayedBefore())
                throw new InvalidValueException(InvalidValueException.PLAYER, name);
            return player;
        });
        registerTypeResolver(World.class, (a, b, parameter) -> {
            String name = a.popForParameter(parameter);
            if (name.equalsIgnoreCase("me")) {
                if (b instanceof Player) return ((Player) b).getWorld();
                throw new ServerNotActiveException();
            }
            World world = Bukkit.getWorld(name);
            if (world == null) throw new InvalidValueException(InvalidValueException.NUMBER, name);
            return world;
        });
        registerTypeResolver(PlayerSelector.class, SelectorParam.INSTANCE);
        registerStaticTabSuggestion("nothing", Collections.emptyList());
        registerTabSuggestion("selectors", (args, sender, command, bukkitCommand) -> {
            List<String> completions = new ArrayList<>();
            completions.add("@a");
            completions.add("@r");
            completions.add("@p");
            for (Player player : Bukkit.getOnlinePlayers())
                completions.add(player.getName());
            return completions;
        });
        registerTabSuggestion("players", (args, sender, command, bukkitCommand) -> null); // we handle that later on.
        registerTabSuggestion("worlds", (args, sender, command, cmd) -> {
            List<String> suggestions = new ArrayList<>();
            for (World world : Bukkit.getWorlds())
                suggestions.add(world.getName());
            return suggestions;
        });
        registerContextResolver(CommandSender.class, (args, subject, parameter) -> ((BukkitSubject) subject).getSender());
        registerContextResolver(ConsoleCommandSender.class, ContextResolver.of(Bukkit.getConsoleSender()));
        registerContextResolver(BukkitCommandSubject.class, (args, sender, parameter) -> (BukkitCommandSubject) sender);
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
                    } else if (Player.class.isAssignableFrom(ptype)) {
                        ia.add(sender.asPlayer());
                    }
                }
                return (Collection<String>) handle.invokeWithArguments(ia);
            });
        }
    }

    @Override public CommandHandler registerCommand(@NotNull Object instance) {
        BukkitHandledCommand command = new BukkitHandledCommand(plugin, this, instance, null, null);
        if (command.getName() != null) addCommand(command);
        setDependencies(instance);
        return this;
    }

    public BukkitCommandHandler registerTabSuggestion(@NotNull String suggestionID, @NotNull TabSuggestionProvider provider) {
        tab.put(n(suggestionID, "id"), n(provider, "provider"));
        return this;
    }

    public BukkitCommandHandler registerStaticTabSuggestion(@NotNull String suggestionID, @NotNull Collection<String> completions) {
        ImmutableList<String> values = ImmutableList.copyOf(completions);
        tab.put(n(suggestionID, "id"), (args, sender, command, bukkitCommand) -> values);
        return this;
    }

    public BukkitCommandHandler registerStaticTabSuggestion(@NotNull String suggestionID, @NotNull String... completions) {
        ImmutableList<String> values = ImmutableList.copyOf(completions);
        tab.put(n(suggestionID, "id"), (args, sender, command, bukkitCommand) -> values);
        return this;
    }

    @Override public @NotNull Plugin getPlugin() {
        return plugin;
    }

    @Override protected void injectValues(Class<?> type, @NotNull CommandSubject sender, @NotNull List<String> args, @NotNull HandledCommand command, @NotNull CommandContext bcmd, List<Object> ia) {
        if (CommandSender.class.isAssignableFrom(type))
            ia.add(((BukkitSubject) sender).getSender());
    }

}
