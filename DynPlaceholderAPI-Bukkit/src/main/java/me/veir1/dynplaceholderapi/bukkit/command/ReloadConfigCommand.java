package me.veir1.dynplaceholderapi.bukkit.command;

import me.veir1.dynplaceholderapi.bukkit.DynPlaceholderAPIBukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ReloadConfigCommand implements CommandExecutor {
    private final DynPlaceholderAPIBukkit dynPlaceholderAPIBukkit;

    private final String commandPermission;

    private final static String NO_PERMISSION = "[DynPlaceholderAPI] No tienes permiso para usar ese comando.";
    private final static String RELOADED_CONFIG = "[DynPlaceholderAPI] Se ha recargado el archivo de configuración.";

    public ReloadConfigCommand(final DynPlaceholderAPIBukkit dynPlaceholderAPIBukkit) {
        this.dynPlaceholderAPIBukkit = dynPlaceholderAPIBukkit;

        commandPermission = dynPlaceholderAPIBukkit.getConfig().getString("admin_permission");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            final Player player = (Player) commandSender;
            if (!player.hasPermission(commandPermission)) {
                player.sendMessage(ChatColor.RED + NO_PERMISSION);
                return true;
            }

            dynPlaceholderAPIBukkit.reloadConfig();
            player.sendMessage(ChatColor.GREEN + RELOADED_CONFIG);
            return true;
        }

        dynPlaceholderAPIBukkit.reloadConfig();
        commandSender.sendMessage(ChatColor.GREEN + RELOADED_CONFIG);
        return false;
    }
}
