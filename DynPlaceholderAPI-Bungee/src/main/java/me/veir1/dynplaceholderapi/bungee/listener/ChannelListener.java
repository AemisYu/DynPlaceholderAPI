package me.veir1.dynplaceholderapi.bungee.listener;

import me.veir1.dynplaceholderapi.bungee.DynPlaceholderAPIBungee;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.*;
import java.util.logging.Level;

public final class ChannelListener implements Listener {
    private final DynPlaceholderAPIBungee dynPlaceholderAPIBungee;

    private final String channelName;
    
    public ChannelListener(final DynPlaceholderAPIBungee dynPlaceholderAPIBungee) {
        this.dynPlaceholderAPIBungee = dynPlaceholderAPIBungee;

        channelName = dynPlaceholderAPIBungee.getConfiguration().getString("channel_names");

        if (channelName == null) {
            dynPlaceholderAPIBungee.getLogger().log(Level.SEVERE, "Invalid channel names provided on config.yml, fatal error occurred !!!!!");
            dynPlaceholderAPIBungee.getProxy().stop();
        }

        dynPlaceholderAPIBungee.getLogger().log(Level.INFO, "Registering channel " + channelName);
        dynPlaceholderAPIBungee.getProxy().registerChannel(channelName);
    }
    
    @EventHandler(priority = 64)
    public void onPluginMessage(final PluginMessageEvent event) {
        final String tag = event.getTag();

        if (tag.equalsIgnoreCase(channelName)) {
            final DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));

            try {
                String channel = in.readUTF(); // Channel requested
                if (channel.equals("UsedIP")) {
                    final ProxiedPlayer player = dynPlaceholderAPIBungee.getProxy().getPlayer(event.getReceiver().toString());
                    final ServerInfo server = player.getServer().getInfo();
                    final String usedIp = player.getPendingConnection().getVirtualHost().getHostString();

                    sendToBukkit(player.getName(), usedIp, server);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
                dynPlaceholderAPIBungee.getLogger().log(Level.SEVERE, "Something went wrong while handling response on channel " + channelName);
            }
        }
    }

    private void sendToBukkit(final String playerName, final String ip, ServerInfo server) {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(stream);

        try {
            out.writeUTF("UsedIP");
            out.writeUTF(playerName);
            out.writeUTF(ip);
        } catch (IOException exception) {
            exception.printStackTrace();
            dynPlaceholderAPIBungee.getLogger().log(Level.SEVERE, "Something went wrong while sending response for " + playerName + " with IP address " + ip + " on server " + server.getName());
        }

        server.sendData(channelName, stream.toByteArray());
    }
}
