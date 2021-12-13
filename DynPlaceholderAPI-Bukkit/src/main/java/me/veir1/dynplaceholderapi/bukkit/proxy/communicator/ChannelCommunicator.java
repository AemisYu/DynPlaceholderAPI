package me.veir1.dynplaceholderapi.bukkit.proxy.communicator;

import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public interface ChannelCommunicator {
    CompletableFuture<String> getUsedIP(Player player);
}
