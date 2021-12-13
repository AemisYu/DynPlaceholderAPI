package me.veir1.dynplaceholderapi.bukkit.variable;

import me.veir1.dynplaceholderapi.bukkit.DynPlaceholderAPIBukkit;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public final class VariableProcessor {
    private final FileConfiguration config;

    public VariableProcessor(final DynPlaceholderAPIBukkit dynPlaceholderAPIBukkit) {
        this.config = dynPlaceholderAPIBukkit.getConfig();
    }

    public String getVariable(final String ip, final String variable) {
        final String result = config.getString("variables." + ip + "." + variable);

        if (result != null) return ChatColor.translateAlternateColorCodes('&', result);

        return "404: [" + ip + " ; " + variable + "] !!";
    }
}
