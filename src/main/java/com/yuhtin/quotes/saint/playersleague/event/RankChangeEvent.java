package com.yuhtin.quotes.saint.playersleague.event;

import com.yuhtin.quotes.saint.playersleague.model.LeagueRank;
import com.yuhtin.quotes.saint.playersleague.model.LeagueUser;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class RankChangeEvent extends LeagueUserEvent {

    private final LeagueRank lastRank;
    private final LeagueRank newRank;

    public RankChangeEvent(Player player, LeagueUser user, LeagueRank lastRank, LeagueRank newRank) {
        super(player, user);
        this.lastRank = lastRank;
        this.newRank = newRank;
    }

}
