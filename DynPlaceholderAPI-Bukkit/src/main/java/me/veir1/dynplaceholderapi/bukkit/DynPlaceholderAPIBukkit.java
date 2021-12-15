package me.veir1.dynplaceholderapi.bukkit;

import lombok.Getter;

import me.veir1.dynplaceholderapi.bukkit.command.PluginCommand;
import me.veir1.dynplaceholderapi.bukkit.command.ReloadConfigCommand;
import me.veir1.dynplaceholderapi.bukkit.listener.PlayerAccessListener;
import me.veir1.dynplaceholderapi.bukkit.placeholderapi.PlaceholderAPIExpansion;
import me.veir1.dynplaceholderapi.bukkit.proxy.RedisMessenger;
import me.veir1.dynplaceholderapi.bukkit.proxy.communicator.ChannelCommunicator;
import me.veir1.dynplaceholderapi.bukkit.player.cache.PlaceholderPlayerCache;
import me.veir1.dynplaceholderapi.bukkit.player.loader.PlaceholderPlayerLoader;

import me.veir1.dynplaceholderapi.bukkit.player.cache.PlaceholderPlayerStorage;
import me.veir1.dynplaceholderapi.bukkit.player.loader.PlaceholderPlayerHelper;
import me.veir1.dynplaceholderapi.bukkit.proxy.ProxyMessenger;
import me.veir1.dynplaceholderapi.bukkit.redis.RedisClient;
import me.veir1.dynplaceholderapi.bukkit.redis.config.RedisClientConfiguration;
import me.veir1.dynplaceholderapi.bukkit.redis.connector.RedisConnection;
import me.veir1.dynplaceholderapi.bukkit.variable.VariableProcessor;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

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

        setupChannelConnection();

        placeholderPlayerCache = new PlaceholderPlayerStorage();
        placeholderPlayerLoader = new PlaceholderPlayerHelper(this);

        variableProcessor = new VariableProcessor(this);

        registerCommand("dynpapi", new PluginCommand());
        registerCommand("dynpapireload", new ReloadConfigCommand(this));

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

    private void registerCommand(final String commandName, final @NotNull CommandExecutor commandExecutor) {
        final org.bukkit.command.PluginCommand command = getServer().getPluginCommand(commandName);
        if (command == null) {
            Bukkit.getLogger().log(Level.WARNING, "Could not register command " + commandName + ", make sure it's registered on plugin.yml file.");
            return;
        }

        command.setExecutor(commandExecutor);
        Bukkit.getLogger().log(Level.WARNING, "Registered command " + commandName + ".");
    }

    private void setupChannelConnection() {
        final String channelType = getConfig().getString("channel_type");
        if (channelType == null) {
            getLogger().log(Level.SEVERE, "Invalid channel type detected. Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        switch (channelType) {
            case "bungee": {
                this.channelCommunicator = new ProxyMessenger(this);
                break;
            }
            case "redis": {
                final RedisClientConfiguration redisClientConfiguration = new RedisClientConfiguration(
                    getConfig().getString("redis.address"),
                    getConfig().getInt("redis.port"),
                    getConfig().getString("redis.password"),
                    getConfig().getString("channel_names")
                );
                final RedisConnection redisConnection = new RedisClient(this, redisClientConfiguration);
                this.channelCommunicator = new RedisMessenger(this, redisConnection);
                break;
            }
            default: {
                getLogger().log(Level.SEVERE, "Invalid channel type detected. Disabling plugin...");
                getServer().getPluginManager().disablePlugin(this);
                break;
            }
        }
    }
}
