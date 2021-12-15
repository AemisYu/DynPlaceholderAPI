package me.veir1.dynplaceholderapi.bukkit.redis;

import me.veir1.dynplaceholderapi.bukkit.DynPlaceholderAPIBukkit;
import me.veir1.dynplaceholderapi.bukkit.redis.config.RedisClientConfiguration;
import me.veir1.dynplaceholderapi.bukkit.redis.connector.RedisConnection;
import me.veir1.dynplaceholderapi.bukkit.redis.pubsub.PubSubMessenger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

public final class RedisClient implements RedisConnection {
    private final RedisClientConfiguration configuration;
    private final Lock redisLock;
    private Jedis jedis;
    private JedisPool jedisPool;

    public RedisClient(DynPlaceholderAPIBukkit dynPlaceholderAPIBukkit, final RedisClientConfiguration redisBaseConfiguration) {
        this.redisLock = new ReentrantLock();
        this.configuration = redisBaseConfiguration;

        if (redisBaseConfiguration.getSubscribeChannel() != null) {
            if (redisBaseConfiguration.getSubscribeChannel().equals("")) {
                dynPlaceholderAPIBukkit.getLogger().log(Level.SEVERE, "Invalid channel_names variable found on config.yml, disabling plugin.");
                dynPlaceholderAPIBukkit.getServer().getPluginManager().disablePlugin(dynPlaceholderAPIBukkit);
                return;
            }
        }

        new PubSubMessenger(dynPlaceholderAPIBukkit, this::getJedis, redisBaseConfiguration);
    }

    @Override
    public Jedis getJedis() {
        redisLock.lock();
        try {
            if (this.jedis == null) this.jedis = new Jedis(this.configuration.getAddress(), this.configuration.getPort());
            return this.jedis;
        } finally {
            redisLock.unlock();
        }
    }

    @Override
    public JedisPool getPool() {
        if (this.jedisPool == null) {
            return this.jedisPool = new JedisPool(this.configuration.getAddress(), this.configuration.getPort());
        }
        return this.jedisPool;
    }

    @Override
    public void publish(final String message) {
        try (final Jedis jedis = getPool().getResource()) {
            if (configuration.getPassword() != null) {
                if (!configuration.getPassword().equals("")) {
                    jedis.auth(configuration.getPassword());
                }
            }
            jedis.publish(configuration.getSubscribeChannel(), message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
