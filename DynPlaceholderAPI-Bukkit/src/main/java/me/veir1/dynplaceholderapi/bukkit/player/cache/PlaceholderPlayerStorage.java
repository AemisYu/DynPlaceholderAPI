package me.veir1.dynplaceholderapi.bukkit.player.cache;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import me.veir1.dynplaceholderapi.bukkit.player.PlaceholderPlayer;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class PlaceholderPlayerStorage implements PlaceholderPlayerCache {
    private final Map<String, PlaceholderPlayer> placeholderPlayerMap = Maps.newConcurrentMap();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public PlaceholderPlayer getPlaceholderPlayer(String playerName) {
        lock.readLock().lock();
        try {
            return placeholderPlayerMap.get(playerName.toLowerCase());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Set<PlaceholderPlayer> getPlaceholderPlayers() {
        lock.readLock().lock();
        try {
            return ImmutableSet.copyOf(placeholderPlayerMap.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void addPlaceholderPlayer(PlaceholderPlayer placeholderPlayer) {
        final String insensitiveName = placeholderPlayer.getCaseInsensitiveName();

        lock.writeLock().lock();
        try {
            placeholderPlayerMap.put(insensitiveName, placeholderPlayer);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void removePlaceholderPlayer(PlaceholderPlayer placeholderPlayer) {
        final String insensitiveName = placeholderPlayer.getCaseInsensitiveName();

        lock.writeLock().lock();
        try {
            placeholderPlayerMap.remove(insensitiveName);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
