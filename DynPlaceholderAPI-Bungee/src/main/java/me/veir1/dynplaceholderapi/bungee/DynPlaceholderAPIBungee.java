package me.veir1.dynplaceholderapi.bungee;

import me.veir1.dynplaceholderapi.bungee.listener.ChannelListener;

import lombok.Getter;

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

        getProxy().getPluginManager().registerListener(this, new ChannelListener(this));

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
}
