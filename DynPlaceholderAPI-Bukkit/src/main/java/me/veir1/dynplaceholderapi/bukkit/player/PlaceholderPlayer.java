package me.veir1.dynplaceholderapi.bukkit.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public final class PlaceholderPlayer {
    private final UUID uuid;
    private final String playerName;
    private String accessAddress;

    public String getCaseInsensitiveName() {
        return playerName.toLowerCase();
    }

    public Player getPlayer() {
        return Bukkit.getPlayerExact(playerName);
    }
}
