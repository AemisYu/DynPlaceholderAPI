package me.veir1.dynplaceholderapi.bungee.redis.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
public final class RedisClientConfiguration {
    private final String address;
    private final Integer port;
    private final String password;
    private final String subscribeChannel;
}
