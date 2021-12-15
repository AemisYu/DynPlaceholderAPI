package me.veir1.dynplaceholderapi.bukkit.redis.connector;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public interface RedisConnection {
     Jedis getJedis();
     JedisPool getPool();

     void publish(String string);
}
