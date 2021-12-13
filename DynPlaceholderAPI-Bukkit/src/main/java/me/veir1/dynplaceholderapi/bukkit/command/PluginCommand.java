package me.veir1.dynplaceholderapi.bukkit.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class PluginCommand implements CommandExecutor {
    private final static String AUTHOR = "DynPlaceholderAPI by @veir1 [ https://github.com/veir1 ]";

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        commandSender.sendMessage(ChatColor.GREEN + AUTHOR);
        return false;
    }
}
