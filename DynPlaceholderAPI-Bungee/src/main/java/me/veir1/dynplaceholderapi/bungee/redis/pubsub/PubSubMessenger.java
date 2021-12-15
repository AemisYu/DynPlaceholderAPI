package me.veir1.dynplaceholderapi.bungee.redis.pubsub;

import me.veir1.dynplaceholderapi.bungee.DynPlaceholderAPIBungee;
import me.veir1.dynplaceholderapi.bungee.redis.config.RedisClientConfiguration;

import me.veir1.dynplaceholderapi.bungee.redis.event.RedisMessageEvent;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.function.Supplier;
import java.util.logging.Level;

public final class PubSubMessenger {
    public PubSubMessenger(final DynPlaceholderAPIBungee dynPlaceholderAPIBungee, final Supplier<Jedis> jedisInstance, final RedisClientConfiguration redisClientConfiguration) {
        final Jedis jedis = jedisInstance.get();
        final JedisPubSub pubSub = new JedisPubSub() {
            @Override
            public void onMessage(final String channel, final String message) {
                dynPlaceholderAPIBungee.getProxy().getPluginManager().callEvent(new RedisMessageEvent(message));
            }
        };

        dynPlaceholderAPIBungee.getProxy().getScheduler().runAsync(dynPlaceholderAPIBungee, () -> {
            if (redisClientConfiguration.getPassword() != null) {
                if (!redisClientConfiguration.getPassword().equals("")) {
                    jedis.auth(redisClientConfiguration.getPassword());
                }
            }
            jedis.subscribe(pubSub, redisClientConfiguration.getSubscribeChannel());
            dynPlaceholderAPIBungee.getLogger().log(Level.INFO, "Started PubSubMessenger subscriber on channel " + redisClientConfiguration.getSubscribeChannel());
        });
    }
}
