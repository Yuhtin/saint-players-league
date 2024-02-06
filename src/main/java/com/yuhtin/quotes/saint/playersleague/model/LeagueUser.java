package com.yuhtin.quotes.saint.playersleague.model;

import com.yuhtin.quotes.saint.playersleague.event.UserPointsChangedEvent;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.lucko.helper.Events;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class LeagueUser {

    private final String username;
    private int points;
    private int rankId;
    private String rankPrefix;

    private final List<String> commandsNotExecuted = new ArrayList<>();

    public void setPoints(int points) {
        int oldPoints = this.points;
        this.points = points;

        Events.call(new UserPointsChangedEvent(Bukkit.getPlayerExact(username), this, oldPoints, points));
    }

    public void setPointsSilent(int points) {
        this.points = points;
    }

    public void addPoints(int points) {
        setPoints(this.points + points);
    }
}