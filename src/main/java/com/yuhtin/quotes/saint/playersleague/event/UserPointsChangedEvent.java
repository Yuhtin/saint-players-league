package com.yuhtin.quotes.saint.playersleague.event;

import com.yuhtin.quotes.saint.playersleague.model.LeagueUser;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class UserPointsChangedEvent extends LeagueUserEvent {

    private final int oldPoints;
    private final int newPoints;

    public UserPointsChangedEvent(Player player, LeagueUser user, int lastPoints, int newPoints) {
        super(player, user);
        this.oldPoints = lastPoints;
        this.newPoints = newPoints;
    }

}
