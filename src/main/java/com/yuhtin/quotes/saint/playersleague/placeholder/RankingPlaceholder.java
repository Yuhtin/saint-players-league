package com.yuhtin.quotes.saint.playersleague.placeholder;

import com.yuhtin.quotes.saint.playersleague.PlayersLeaguePlugin;
import com.yuhtin.quotes.saint.playersleague.model.LeagueUser;
import lombok.AllArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class RankingPlaceholder extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "playersleagueranking";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Yuhtin";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        String[] split = params.split("-");
        String type = split[0];
        int position = Integer.parseInt(split[1]);

        LeagueUser leagueUser = PlayersLeaguePlugin.getInstance().getController().getByPosition(position);
        return type.equalsIgnoreCase("name")
                ? leagueUser.getUsername()
                : String.valueOf(leagueUser.getPoints());
    }
}
