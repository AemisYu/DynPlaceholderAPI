package me.veir1.dynplaceholderapi.bukkit.redis.pubsub;

import me.veir1.dynplaceholderapi.bukkit.DynPlaceholderAPIBukkit;
import me.veir1.dynplaceholderapi.bukkit.redis.config.RedisClientConfiguration;
import me.veir1.dynplaceholderapi.bukkit.redis.event.RedisMessageEvent;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.function.Supplier;
import java.util.logging.Level;

public final class PubSubMessenger {
    public PubSubMessenger(final DynPlaceholderAPIBukkit dynPlaceholderAPIBukkit, final Supplier<Jedis> jedisInstance, final RedisClientConfiguration redisClientConfiguration) {
        final Jedis jedis = jedisInstance.get();
        final JedisPubSub pubSub = new JedisPubSub() {
            @Override
            public void onMessage(final String channel, final String message) {
                Bukkit.getPluginManager().callEvent(new RedisMessageEvent(channel, message));
            }
        };

        Bukkit.getScheduler().runTaskAsynchronously(dynPlaceholderAPIBukkit, () -> {
            if (redisClientConfiguration.getPassword() != null) {
                if (!redisClientConfiguration.getPassword().equals("")) {
                    jedis.auth(redisClientConfiguration.getPassword());
                }
            }
            jedis.subscribe(pubSub, redisClientConfiguration.getSubscribeChannel());
            dynPlaceholderAPIBukkit.getLogger().log(Level.INFO, "Started PubSubMessenger subscriber on channel " + redisClientConfiguration.getSubscribeChannel());
        });
    }
}
