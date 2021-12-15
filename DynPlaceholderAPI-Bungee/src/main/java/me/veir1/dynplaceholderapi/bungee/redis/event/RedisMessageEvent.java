package me.veir1.dynplaceholderapi.bungee.redis.event;

import net.md_5.bungee.api.plugin.Event;

public class RedisMessageEvent extends Event {
    private final String receivedMessage;

    public RedisMessageEvent(final String receivedMessage) {
        this.receivedMessage = receivedMessage;
    }

    public String getReceivedMessage() {
        return this.receivedMessage;
    }
}
