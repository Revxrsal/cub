package io.github.revxrsal.cub.bungee.core;

import io.github.revxrsal.cub.ArgumentStack;
import io.github.revxrsal.cub.CommandParameter;
import io.github.revxrsal.cub.HandledCommand;
import io.github.revxrsal.cub.ParameterResolver;
import io.github.revxrsal.cub.bungee.BungeeCommandSubject;
import io.github.revxrsal.cub.bungee.BungeeHandledCommand;
import io.github.revxrsal.cub.bungee.TabSuggestionProvider;
import io.github.revxrsal.cub.bungee.annotation.CommandPermission;
import io.github.revxrsal.cub.bungee.annotation.TabCompletion;
import io.github.revxrsal.cub.core.BaseCommandHandler;
import io.github.revxrsal.cub.core.BaseHandledCommand;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

import static io.github.revxrsal.cub.core.BaseDispatcher.SPLIT;
import static io.github.revxrsal.cub.core.Utils.c;

final class BungeeCommand extends BaseHandledCommand implements BungeeHandledCommand {

    private static final Pattern BY_WALL = Pattern.compile("|");
    private final List<TabSuggestionProvider> tabCompletions = new ArrayList<>();

    public BungeeCommand(Plugin plugin, BungeeHandler handler, Object instance, @Nullable BaseHandledCommand parent, @Nullable AnnotatedElement ae) {
        super(handler, instance, parent, ae);
        setProperties2();
        if (parent == null && name != null)
            registerCommandToBungee(handler, plugin, this);
    }

    private void setProperties2() {
        TabCompletion tc = annReader.get(TabCompletion.class);
        List<String> completions = tc == null || tc.value().isEmpty() ? Collections.emptyList() : Arrays.asList(SPLIT.split(tc.value()));
        if (completions.isEmpty()) {
            for (CommandParameter parameter : getParameters()) {
                if (parameter.isSwitch() || parameter.isFlag()) continue;
                if (parameter.getResolver() instanceof ParameterResolver.ContextResolver ||
                        (parameter.getMethodIndex() == 0 && BungeeDispatcher.isSender(parameter.getType()))) continue;
                TabSuggestionProvider found = ((BungeeHandler) handler).getTabs(parameter.getType());
                tabCompletions.add(found);
            }
        } else {
            for (String id : completions) {
                if (id.startsWith("@")) {
                    tabCompletions.add(c(((BungeeHandler) handler).tab.get(id), "Invalid tab completion ID: " + id));
                } else {
                    List<String> values = Arrays.asList(BY_WALL.split(id));
                    tabCompletions.add((args, sender, command, bungeeCommand) -> values);
                }
            }
        }
        CommandPermission permission = annReader.get(CommandPermission.class);
        if (permission != null) {
            String node = permission.value();
            this.permission = sender -> ((BungeeCommandSubject) sender).getSender().hasPermission(node);
        }
    }

    public static void registerCommandToBungee(BungeeHandler handler, Plugin plugin, HandledCommand command) {
        BungeeDispatcher dispatcher = new BungeeDispatcher(handler);
        BungeeCmd cmd = new BungeeCmd(dispatcher, handler, command.getName(), command.getAliases().toArray(new String[0]));
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, cmd);
    }

    @Override protected BaseHandledCommand newCommand(BaseCommandHandler handler, Object o, BaseHandledCommand parent, Class<?> innerClass) {
        return new BungeeCommand(((BungeeHandler) handler).plugin, (BungeeHandler) handler, o, parent, innerClass);
    }

    @Override protected BaseHandledCommand newCommand(BaseCommandHandler handler, Object o, BaseHandledCommand parent, Method method) {
        return new BungeeCommand(((BungeeHandler) handler).plugin, (BungeeHandler) handler, o, parent, method);
    }

    @NotNull Collection<String> resolveTab(ArgumentStack args, BungeeCommandSubject sender, Command bungeeCommand) {
        if (isPrivate() || !permission.canExecute(sender)) return Collections.emptyList();
        if (tabCompletions.isEmpty() || args.size() == 0) return Collections.emptyList();
        int index = args.size() - 1;
        try {
            return tabCompletions.get(index)
                    .getSuggestions(args.asImmutableList(), sender, this, bungeeCommand);
        } catch (Throwable e) {
            return Collections.emptyList();
        }
    }

    @Override public @NotNull List<TabSuggestionProvider> getTabCompletions() {
        return tabCompletions;
    }
}
