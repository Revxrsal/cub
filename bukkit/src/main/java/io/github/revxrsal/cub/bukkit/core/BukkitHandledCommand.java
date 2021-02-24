package io.github.revxrsal.cub.bukkit.core;

import io.github.revxrsal.cub.ArgumentStack;
import io.github.revxrsal.cub.HandledCommand;
import io.github.revxrsal.cub.bukkit.BukkitCommandSubject;
import io.github.revxrsal.cub.bukkit.annotation.CommandPermission;
import io.github.revxrsal.cub.bukkit.annotation.TabCompletion;
import io.github.revxrsal.cub.core.BaseCommandHandler;
import io.github.revxrsal.cub.core.BaseHandledCommand;
import io.github.revxrsal.cub.core.Utils;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static io.github.revxrsal.cub.core.BaseDispatcher.SPLIT;

@Getter
class BukkitHandledCommand extends BaseHandledCommand implements io.github.revxrsal.cub.bukkit.BukkitHandledCommand {

    private List<String> tabCompletions;

    public BukkitHandledCommand(Plugin plugin, BukkitHandler handler, Object instance, @Nullable BaseHandledCommand parent, @Nullable AnnotatedElement ae) {
        super(handler, instance, parent, ae);
        if (parent == null && name != null)
            registerCommandToBukkit(handler, plugin, this);
    }

    @Override protected void setProperties() {
        tabCompletions = Arrays.asList(SPLIT.split(annReader.get(TabCompletion.class, TabCompletion::value, "")));
        CommandPermission permission = annReader.get(CommandPermission.class);
        if (permission != null) {
            Permission p = new Permission(permission.value(), permission.access());
            this.permission = sender -> ((BukkitSubject) sender).getSender().hasPermission(p);
        }
    }

    public static void registerCommandToBukkit(BukkitHandler handler, Plugin plugin, HandledCommand command) {
        try {
            PluginCommand cmd = commandConstructor.newInstance(command.getName(), plugin);
            commandMap.register(plugin.getName(), cmd);
            BukkitDispatcher dispatcher = new BukkitDispatcher(handler);
            cmd.setExecutor(dispatcher);
            cmd.setTabCompleter(new BukkitTab(handler));
            cmd.setDescription(command.getDescription() == null ? "" : command.getDescription());
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @Override protected BaseHandledCommand newCommand(BaseCommandHandler handler, Object o, BaseHandledCommand parent, Class<?> innerClass) {
        return new BukkitHandledCommand(((BukkitHandler) handler).plugin, (BukkitHandler) handler, o, parent, innerClass);
    }

    @Override protected BaseHandledCommand newCommand(BaseCommandHandler handler, Object o, BaseHandledCommand parent, Method method) {
        return new BukkitHandledCommand(((BukkitHandler) handler).plugin, (BukkitHandler) handler, o, parent, method);
    }

    private static Constructor<PluginCommand> commandConstructor;
    private static CommandMap commandMap;

    static {
        try {
            commandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            commandConstructor.setAccessible(true);
            Field cmdf = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            Utils.ensureAccessible(cmdf);
            commandMap = (CommandMap) cmdf.get(Bukkit.getServer());
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @NotNull Collection<String> resolveTab(ArgumentStack args, BukkitCommandSubject sender, org.bukkit.command.Command bukkitCommand) {
        if (tabCompletions.isEmpty() || args.size() == 0) return Collections.emptyList();
        int index = args.size() - 1;
        try {
            String found = tabCompletions.get(index);
            if (found.startsWith("@")) {
                Collection<String> tab = ((BukkitHandler) handler).tab.get(found.substring(1))
                        .getSuggestions(args.asImmutableList(), sender, this, bukkitCommand);
                if (tab == null) return BukkitTab.playerList(args.get(args.size() - 1), sender);
                return tab;
            } else {
                return Arrays.asList(StringUtils.split(found, '|'));
            }
        } catch (Throwable e) {
            return Collections.emptyList();
        }
    }
}
