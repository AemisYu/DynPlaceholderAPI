package me.veir1.dynplaceholderapi.bukkit.player.loader;

import me.veir1.dynplaceholderapi.bukkit.player.PlaceholderPlayer;
import org.bukkit.entity.Player;

public interface PlaceholderPlayerLoader {
    PlaceholderPlayer getOrCreatePlayer(Player player);
    PlaceholderPlayer getCachedPlayer(Player player);
    void clearPlayer(String playerName);
}
