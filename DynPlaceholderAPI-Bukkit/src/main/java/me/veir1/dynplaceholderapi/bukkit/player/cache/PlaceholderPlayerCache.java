package me.veir1.dynplaceholderapi.bukkit.player.cache;

import me.veir1.dynplaceholderapi.bukkit.player.PlaceholderPlayer;

import java.util.Set;

public interface PlaceholderPlayerCache {
    PlaceholderPlayer getPlaceholderPlayer(String playerName);
    Set<PlaceholderPlayer> getPlaceholderPlayers();
    void addPlaceholderPlayer(PlaceholderPlayer gamePlayer);
    void removePlaceholderPlayer(PlaceholderPlayer gamePlayer);
}