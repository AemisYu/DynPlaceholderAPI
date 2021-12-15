package me.veir1.dynplaceholderapi.bungee;

import me.veir1.dynplaceholderapi.bungee.listener.BungeeChannelListener;

import lombok.Getter;

import me.veir1.dynplaceholderapi.bungee.listener.RedisChannelListener;
import me.veir1.dynplaceholderapi.bungee.redis.RedisClient;
import me.veir1.dynplaceholderapi.bungee.redis.config.RedisClientConfiguration;
import me.veir1.dynplaceholderapi.bungee.redis.connector.RedisConnection;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

public final class DynPlaceholderAPIBungee extends Plugin {
    @Getter private static DynPlaceholderAPIBungee dynPlaceholderAPIBungee;

    @Getter private Configuration configuration;

    @Getter private RedisConnection redisConnection;

    @Override
    public void onEnable() {
        dynPlaceholderAPIBungee = this;

        getLogger().log(Level.INFO, "Loading...");

        // Configuration stuff, blame BungeeCord for not providing a default YAML config like Bukkit
        makeConfig();

        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException exception) {
            getLogger().log(Level.SEVERE, "Could not load configuration file, fatal error occurred !!!!!!");
        }

        setupChannelConnection();

        getLogger().log(Level.INFO, "Done.");
    }

    @Override
    public void onDisable() {
        dynPlaceholderAPIBungee = null;
    }

    private void makeConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        final File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupChannelConnection() {
        final String channelType = configuration.getString("channel_type");
        if (channelType == null) {
            getLogger().log(Level.SEVERE, "Invalid channel type detected. Disabling proxy...");
            getProxy().stop();
            return;
        }
        switch (channelType) {
            case "bungee": {
                getProxy().getPluginManager().registerListener(this, new BungeeChannelListener(this));
                break;
            }
            case "redis": {
                final RedisClientConfiguration redisClientConfiguration = new RedisClientConfiguration(
                        configuration.getString("redis.address"),
                        configuration.getInt("redis.port"),
                        configuration.getString("redis.password"),
                        configuration.getString("channel_names")
                );
                this.redisConnection = new RedisClient(this, redisClientConfiguration);
                getProxy().getPluginManager().registerListener(this, new RedisChannelListener(this));
                break;
            }
            default: {
                getLogger().log(Level.SEVERE, "Invalid channel type detected. Disabling proxy...");
                getProxy().stop();
                break;
            }
        }
    }
}
