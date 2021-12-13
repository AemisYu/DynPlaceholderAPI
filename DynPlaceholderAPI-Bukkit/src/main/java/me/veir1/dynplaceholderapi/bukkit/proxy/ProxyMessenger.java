package me.veir1.dynplaceholderapi.bukkit.proxy;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.veir1.dynplaceholderapi.bukkit.DynPlaceholderAPIBukkit;
import me.veir1.dynplaceholderapi.bukkit.proxy.communicator.ChannelCommunicator;
import me.veir1.dynplaceholderapi.bukkit.util.CallbackUtil;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class ProxyMessenger implements ChannelCommunicator {
    private final DynPlaceholderAPIBukkit dynPlaceholderAPIBukkit;
    private final PluginMessageListener messageListener;

    private final Map<String, Queue<CompletableFuture<?>>> callbackMap;

    private String outgoingChannelName;
    private String incomingChannelName;

    public ProxyMessenger(final DynPlaceholderAPIBukkit plugin) {
        this.dynPlaceholderAPIBukkit = plugin;
        this.callbackMap = new ConcurrentHashMap<>();
        this.messageListener = this::onPluginMessageReceived;

        final String channelNames = plugin.getConfig().getString("channel_names");

        if (channelNames == null) {
            plugin.getLogger().log(Level.SEVERE, "Invalid channel_names variable found on config.yml, disabling plugin.");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        this.outgoingChannelName = plugin.getConfig().getString("channel_names");
        this.incomingChannelName = plugin.getConfig().getString("channel_names");

        final Messenger messenger = plugin.getServer().getMessenger();
        messenger.registerOutgoingPluginChannel(plugin, outgoingChannelName);
        messenger.registerIncomingPluginChannel(plugin, incomingChannelName, messageListener);
    }

    public CompletableFuture<String> getUsedIP(final Player player) {
        final CompletableFuture<String> future = new CompletableFuture<>();

        synchronized (callbackMap) {
            callbackMap.compute("UsedIP-" + player.getName(), CallbackUtil.computeValue(future));
        }

        final ByteArrayDataOutput output = ByteStreams.newDataOutput();

        output.writeUTF("UsedIP");
        player.sendPluginMessage(this.dynPlaceholderAPIBukkit, outgoingChannelName, output.toByteArray());
        return future;
    }

    @SuppressWarnings("unchecked")
    private void onPluginMessageReceived(final String channel, final Player player, final byte[] message) {
        if (!channel.equalsIgnoreCase(incomingChannelName)) return;

        final ByteArrayDataInput input = ByteStreams.newDataInput(message);
        final String subChannel = input.readUTF();

        synchronized (callbackMap) {
            final Queue<CompletableFuture<?>> callbacks;

            if (subChannel.equals("UsedIP")) {
                String playerName = input.readUTF(); // player name
                String playerUsedIp = input.readUTF(); // player ip

                callbacks = callbackMap.get(subChannel + "-" + playerName);

                if (callbacks == null || callbacks.isEmpty())  {
                    Bukkit.getLogger().log(Level.SEVERE, "Something went wrong trying to handle UsedIP subchannel.");
                    return;
                }

                final CompletableFuture<?> callback = callbacks.poll();

                try {
                    ((CompletableFuture<String>) callback).complete(playerUsedIp);
                } catch (final Exception ex) {
                    callback.completeExceptionally(ex);
                }
            }
        }
    }
}
