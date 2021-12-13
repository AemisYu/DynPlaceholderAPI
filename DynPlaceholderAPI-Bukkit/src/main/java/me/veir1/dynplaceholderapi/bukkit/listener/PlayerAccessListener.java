package me.veir1.dynplaceholderapi.bukkit.listener;

import me.veir1.dynplaceholderapi.bukkit.DynPlaceholderAPIBukkit;
import me.veir1.dynplaceholderapi.bukkit.player.loader.PlaceholderPlayerLoader;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.logging.Level;

public final class PlayerAccessListener implements Listener {
    private final PlaceholderPlayerLoader placeholderPlayerLoader;

    private final static long LOAD_DELAY = DynPlaceholderAPIBukkit.getDynPlaceholderAPIBukkit().getConfig().getLong("load_delay");

    public PlayerAccessListener(final DynPlaceholderAPIBukkit placeholderAPIBukkit) {
        this.placeholderPlayerLoader = placeholderAPIBukkit.getPlaceholderPlayerLoader();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerAccess(final PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(DynPlaceholderAPIBukkit.getDynPlaceholderAPIBukkit(), () -> {
            final Player player = event.getPlayer();
            Bukkit.getLogger().log(Level.INFO, "Loading player props " + player.getName());
            placeholderPlayerLoader.getOrCreatePlayer(player);
        }, LOAD_DELAY);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLeave(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        placeholderPlayerLoader.clearPlayer(player.getName());
    }
}
