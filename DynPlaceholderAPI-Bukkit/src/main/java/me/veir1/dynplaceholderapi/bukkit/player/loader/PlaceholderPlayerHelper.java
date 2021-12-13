package me.veir1.dynplaceholderapi.bukkit.player.loader;

import me.veir1.dynplaceholderapi.bukkit.DynPlaceholderAPIBukkit;
import me.veir1.dynplaceholderapi.bukkit.player.PlaceholderPlayer;
import me.veir1.dynplaceholderapi.bukkit.player.cache.PlaceholderPlayerCache;
import me.veir1.dynplaceholderapi.bukkit.proxy.communicator.ChannelCommunicator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public final class PlaceholderPlayerHelper implements PlaceholderPlayerLoader {
    private final PlaceholderPlayerCache placeholderPlayerCache;
    private final ChannelCommunicator channelCommunicator;

    public PlaceholderPlayerHelper(DynPlaceholderAPIBukkit dynPlaceholderAPIBukkit) {
        this.placeholderPlayerCache = dynPlaceholderAPIBukkit.getPlaceholderPlayerCache();
        this.channelCommunicator = dynPlaceholderAPIBukkit.getChannelCommunicator();
    }

    public PlaceholderPlayer getOrCreatePlayer(final Player player) {
        if (placeholderPlayerCache.getPlaceholderPlayer(player.getName()) != null) {
            if (placeholderPlayerCache.getPlaceholderPlayer(player.getName()).getAccessAddress() != null) {
                return placeholderPlayerCache.getPlaceholderPlayer(player.getName());
            }
        }

        final PlaceholderPlayer placeholderPlayer = new PlaceholderPlayer(player.getUniqueId(), player.getName());

        final CompletableFuture<String> usedIpFuture = channelCommunicator.getUsedIP(player);
        usedIpFuture.whenComplete((response, throwable) -> {
            placeholderPlayerCache.getPlaceholderPlayer(placeholderPlayer.getPlayerName()).setAccessAddress(response);
            Bukkit.getLogger().log(Level.INFO, "(Response) Loaded ip for " + placeholderPlayer.getCaseInsensitiveName() + ": " + response);
        });

        placeholderPlayerCache.addPlaceholderPlayer(placeholderPlayer);

        return placeholderPlayerCache.getPlaceholderPlayer(placeholderPlayer.getPlayerName());
    }

    public PlaceholderPlayer getCachedPlayer(final Player player) {
        return placeholderPlayerCache.getPlaceholderPlayer(player.getName());
    }

    public void clearPlayer(final String playerName) {
        if (placeholderPlayerCache.getPlaceholderPlayer(playerName) != null) {
            Bukkit.getLogger().log(Level.INFO, "Cleaning ip address for " + playerName);
            placeholderPlayerCache.removePlaceholderPlayer(placeholderPlayerCache.getPlaceholderPlayer(playerName));
            Bukkit.getLogger().log(Level.INFO, "Done cleaning ip address for " + playerName);
        }
    }
}
