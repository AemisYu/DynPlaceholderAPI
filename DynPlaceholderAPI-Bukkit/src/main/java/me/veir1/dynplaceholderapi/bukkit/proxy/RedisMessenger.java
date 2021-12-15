package me.veir1.dynplaceholderapi.bukkit.proxy;

import me.veir1.dynplaceholderapi.bukkit.DynPlaceholderAPIBukkit;
import me.veir1.dynplaceholderapi.bukkit.proxy.communicator.ChannelCommunicator;
import me.veir1.dynplaceholderapi.bukkit.redis.connector.RedisConnection;
import me.veir1.dynplaceholderapi.bukkit.redis.event.RedisMessageEvent;
import me.veir1.dynplaceholderapi.bukkit.util.CallbackUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class RedisMessenger implements ChannelCommunicator, Listener {
    private final Map<String, Queue<CompletableFuture<?>>> callbackMap;
    private final RedisConnection redisConnection;

    public RedisMessenger(final DynPlaceholderAPIBukkit dynPlaceholderAPIBukkit, final RedisConnection redisConnection) {
        this.callbackMap = new ConcurrentHashMap<>();
        this.redisConnection = redisConnection;

        dynPlaceholderAPIBukkit.getServer().getPluginManager().registerEvents(this, dynPlaceholderAPIBukkit);
    }

    @Override
    public CompletableFuture<String> getUsedIP(Player player) {
        final CompletableFuture<String> future = new CompletableFuture<>();

        synchronized (callbackMap) {
            callbackMap.compute("UsedIP-" + player.getName(), CallbackUtil.computeValue(future));
        }

        redisConnection.publish("dynpapibungee;" + player.getName());

        return future;
    }

    @SuppressWarnings("unchecked")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onMessageHandle(RedisMessageEvent redisMessageEvent) {
        final String message = redisMessageEvent.getReceivedMessage();
        if (!message.startsWith("dynpapibukkit;")) return;

        final String[] messageSplit = message.split(";");
        final String subChannel = "UsedIP";
        final String playerName = messageSplit[1];
        final String playerUsedIp = messageSplit[2];

        if (Bukkit.getPlayerExact(playerName) == null) return;

        synchronized (callbackMap) {
            final Queue<CompletableFuture<?>> callbacks;
            callbacks = callbackMap.get(subChannel + "-" + playerName);

            if (callbacks == null || callbacks.isEmpty())  {
                Bukkit.getLogger().log(Level.SEVERE, "Something went wrong trying to handle UsedIP subchannel on Redis.");
                return;
            }

            final CompletableFuture<?> callback = callbacks.poll();

            try {
                ((CompletableFuture<String>) callback).complete(playerUsedIp);
            } catch (final Exception ex) {
                callback.completeExceptionally(ex);
            }
        }
    }
}
