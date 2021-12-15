package me.veir1.dynplaceholderapi.bungee.listener;

import me.veir1.dynplaceholderapi.bungee.DynPlaceholderAPIBungee;
import me.veir1.dynplaceholderapi.bungee.redis.event.RedisMessageEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public final class RedisChannelListener implements Listener {
    private final DynPlaceholderAPIBungee dynPlaceholderAPIBungee;

    public RedisChannelListener(final DynPlaceholderAPIBungee dynPlaceholderAPIBungee) {
        this.dynPlaceholderAPIBungee = dynPlaceholderAPIBungee;
    }

    @EventHandler(priority = 64)
    public void onMessageHandle(final RedisMessageEvent redisMessageEvent) {
        final String message = redisMessageEvent.getReceivedMessage();
        if (!message.startsWith("dynpapibungee;")) return;

        final String[] messageSplit = message.split(";");
        final String playerName = messageSplit[1];

        final ProxiedPlayer player = dynPlaceholderAPIBungee.getProxy().getPlayer(playerName);
        if (player == null) return;

        final String usedIp = player.getPendingConnection().getVirtualHost().getHostString();

        sendToBukkit(player.getName(), usedIp);
    }

    private void sendToBukkit(final String playerName, final String ip) {
        final String message = "dynpapibukkit;" + playerName + ";" + ip;

        dynPlaceholderAPIBungee.getRedisConnection().publish(message);
    }
}
