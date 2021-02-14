package io.github.revxrsal.cub.bukkit.core;

import com.google.common.collect.ImmutableList;
import io.github.revxrsal.cub.ArgumentStack;
import io.github.revxrsal.cub.CommandParameter;
import io.github.revxrsal.cub.HandledCommand;
import io.github.revxrsal.cub.bukkit.BukkitCommandSubject;
import io.github.revxrsal.cub.core.LinkedArgumentStack;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

final class BukkitTab implements TabCompleter {

    private final BukkitHandler handler;

    public BukkitTab(BukkitHandler handler) {
        this.handler = handler;
    }

    @Override public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command bukkitCommand, @NotNull String alias, String[] args) {
        ArgumentStack stack = new LinkedArgumentStack(handler, args);
        if (stack.isEmpty()) return ImmutableList.of();
        Set<String> completions = new HashSet<>();
        HandledCommand parent = handler.getCommands().get(bukkitCommand.getName());
        if (parent == null) return ImmutableList.of();
        if (args.length == 1) {
            for (HandledCommand subcommand : parent.getSubcommands().values())
                completions.add(subcommand.getName());
        }
        HandledCommand found = findCommand(parent, stack);
        for (HandledCommand subcommand : found.getSubcommands().values())
            completions.add(subcommand.getName());
        if (!((BukkitHandledCommand) found).getTabCompletions().isEmpty()) {
            completions.addAll(((BukkitHandledCommand) found).resolveTab(stack, new BukkitSubject(sender), bukkitCommand));
        }
        String last = stack.asImmutableList().get(stack.asImmutableList().size() - 1);
        if (last.startsWith(handler.getSwitchPrefix())) {
            for (CommandParameter parameter : found.getParameters())
                if (parameter.isSwitch())
                    completions.add(handler.getSwitchPrefix() + parameter.getSwitchName());
        }
        List<String> completionList = new ArrayList<>(completions);
        completionList.sort(String.CASE_INSENSITIVE_ORDER);
        return completions.stream()
                .filter(c -> StringUtil.startsWithIgnoreCase(c, last))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }

    private HandledCommand findCommand(@NotNull HandledCommand search, @NotNull ArgumentStack stack) {
        try {
            String nextSearch = stack.getFirst();
            HandledCommand found = search.getSubcommands().get(nextSearch);
            if (found != null) {
                stack.pop();
                return findCommand(found, stack);
            }
            return search;
        } catch (NoSuchElementException e) {
            return search;
        }
    }

    // identical behavior to what Bukkit does. we just don't need null values
    static List<String> playerList(@NotNull String lastWord, @NotNull BukkitCommandSubject sender) {
        Player senderPlayer = sender.asPlayer();
        List<String> players = new ArrayList<>();
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            String name = player.getName();
            if ((senderPlayer == null || senderPlayer.canSee(player)) && StringUtil.startsWithIgnoreCase(name, lastWord)) {
                players.add(name);
            }
        }
        return players;
    }
}
