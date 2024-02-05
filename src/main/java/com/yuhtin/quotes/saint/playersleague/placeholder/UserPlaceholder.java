package com.yuhtin.quotes.saint.playersleague.placeholder;

import com.yuhtin.quotes.saint.playersleague.PlayersLeaguePlugin;
import com.yuhtin.quotes.saint.playersleague.model.LeagueUser;
import com.yuhtin.quotes.saint.playersleague.redis.callback.CacheCallback;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UserPlaceholder extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "players-league";
    }

    @Override
    public String getAuthor() {
        return "Yuhtin";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        PlayersLeaguePlugin plugin = PlayersLeaguePlugin.getInstance();
        if (params.equalsIgnoreCase("points")) {
            return String.valueOf(plugin.getController().getPoints(player.getName()));
        }

        if (params.equalsIgnoreCase("rank")) {
            CacheCallback<LeagueUser> callback = plugin.getController().findIfPresent(player.getName());
            return callback.isPresent() ? callback.getUncachedValue().getRankPrefix() : "";
        }

        return "&cPlaceholder inválida";
    }
}
