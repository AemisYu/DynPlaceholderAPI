package me.veir1.dynplaceholderapi.bungee.redis;

import me.veir1.dynplaceholderapi.bungee.DynPlaceholderAPIBungee;
import me.veir1.dynplaceholderapi.bungee.redis.config.RedisClientConfiguration;
import me.veir1.dynplaceholderapi.bungee.redis.connector.RedisConnection;
import me.veir1.dynplaceholderapi.bungee.redis.pubsub.PubSubMessenger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class RedisClient implements RedisConnection {
    private final RedisClientConfiguration configuration;
    private final Lock redisLock;
    private Jedis jedis;
    private JedisPool jedisPool;

    public RedisClient(DynPlaceholderAPIBungee dynPlaceholderAPIBungee, final RedisClientConfiguration redisBaseConfiguration) {
        this.redisLock = new ReentrantLock();
        this.configuration = redisBaseConfiguration;

        new PubSubMessenger(dynPlaceholderAPIBungee, this::getJedis, redisBaseConfiguration);
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
