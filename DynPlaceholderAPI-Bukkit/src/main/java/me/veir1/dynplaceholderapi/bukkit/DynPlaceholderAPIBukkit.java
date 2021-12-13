package me.veir1.dynplaceholderapi.bukkit;

import lombok.Getter;

import me.veir1.dynplaceholderapi.bukkit.command.PluginCommand;
import me.veir1.dynplaceholderapi.bukkit.command.ReloadConfigCommand;
import me.veir1.dynplaceholderapi.bukkit.listener.PlayerAccessListener;
import me.veir1.dynplaceholderapi.bukkit.placeholderapi.PlaceholderAPIExpansion;
import me.veir1.dynplaceholderapi.bukkit.proxy.communicator.ChannelCommunicator;
import me.veir1.dynplaceholderapi.bukkit.player.cache.PlaceholderPlayerCache;
import me.veir1.dynplaceholderapi.bukkit.player.loader.PlaceholderPlayerLoader;

import me.veir1.dynplaceholderapi.bukkit.player.cache.PlaceholderPlayerStorage;
import me.veir1.dynplaceholderapi.bukkit.player.loader.PlaceholderPlayerHelper;
import me.veir1.dynplaceholderapi.bukkit.proxy.ProxyMessenger;

import me.veir1.dynplaceholderapi.bukkit.variable.VariableProcessor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class DynPlaceholderAPIBukkit extends JavaPlugin {
    @Getter private static DynPlaceholderAPIBukkit dynPlaceholderAPIBukkit;

    @Getter private ChannelCommunicator channelCommunicator;
    @Getter private PlaceholderPlayerLoader placeholderPlayerLoader;
    @Getter private PlaceholderPlayerCache placeholderPlayerCache;

    @Getter private VariableProcessor variableProcessor;

    @Override
    public void onEnable() {
        dynPlaceholderAPIBukkit = this;
        getLogger().log(Level.INFO, "Loading...");

        saveDefaultConfig();

        channelCommunicator = new ProxyMessenger(this);
        placeholderPlayerCache = new PlaceholderPlayerStorage();
        placeholderPlayerLoader = new PlaceholderPlayerHelper(this);

        variableProcessor = new VariableProcessor(this);

        getServer().getPluginCommand("dynpapi").setExecutor(new PluginCommand());
        getServer().getPluginCommand("dynpapireload").setExecutor(new ReloadConfigCommand(this));

        if (getConfig().getBoolean("support.placeholderapi")) {
            if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
                getLogger().log(Level.SEVERE, "Could not find PlaceholderAPI, disabling plugin. !! Either disable PlaceholderAPI support on config.yml or download PlaceholderAPI plugin and enable it.");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }

            getLogger().log(Level.INFO, "Registering PlaceholderAPI hook...");
            new PlaceholderAPIExpansion(this).register();
        }

        getServer().getPluginManager().registerEvents(new PlayerAccessListener(this), this);
        getLogger().log(Level.INFO, "Done.");
    }

    @Override
    public void onDisable() {
        dynPlaceholderAPIBukkit = null;
    }
}
