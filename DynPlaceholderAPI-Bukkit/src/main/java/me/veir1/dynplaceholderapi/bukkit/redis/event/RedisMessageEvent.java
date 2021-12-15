package me.veir1.dynplaceholderapi.bukkit.redis.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RedisMessageEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String channel;
    private final String receivedMessage;

    public RedisMessageEvent(final String channel, final String receivedMessage) {
        this.channel = channel;
        this.receivedMessage = receivedMessage;
    }

    public static HandlerList getHandlerList() {
        return RedisMessageEvent.handlers;
    }

    public @NotNull HandlerList getHandlers() {
        return RedisMessageEvent.handlers;
    }

    public String getChannel() {
        return this.channel;
    }

    public String getReceivedMessage() {
        return this.receivedMessage;
    }
}
