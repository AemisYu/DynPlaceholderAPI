package me.veir1.dynplaceholderapi.bukkit.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.veir1.dynplaceholderapi.bukkit.DynPlaceholderAPIBukkit;
import me.veir1.dynplaceholderapi.bukkit.player.PlaceholderPlayer;
import org.bukkit.entity.Player;

public final class PlaceholderAPIExpansion extends PlaceholderExpansion {
    private final DynPlaceholderAPIBukkit dynPlaceholderAPIBukkit;

    public PlaceholderAPIExpansion(DynPlaceholderAPIBukkit dynPlaceholderAPIBukkit) {
        this.dynPlaceholderAPIBukkit = dynPlaceholderAPIBukkit;
    }

    @Override
    public String getAuthor() {
        return "veir1";
    }

    @Override
    public String getIdentifier() {
        return "dynpapi";
    }

    @Override
    public String getVersion() {
        return dynPlaceholderAPIBukkit.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(final Player player, final String params) {
        if (params == null) return "...";

        final PlaceholderPlayer placeholderPlayer = dynPlaceholderAPIBukkit.getPlaceholderPlayerLoader().getCachedPlayer(player);

        if (placeholderPlayer == null) return dynPlaceholderAPIBukkit.getVariableProcessor().getVariable("default", params);
        if (placeholderPlayer.getAccessAddress() == null) return dynPlaceholderAPIBukkit.getVariableProcessor().getVariable("default", params);

        return dynPlaceholderAPIBukkit.getVariableProcessor().getVariable(placeholderPlayer.getAccessAddress().replace(".", "-"), params);
    }
}
