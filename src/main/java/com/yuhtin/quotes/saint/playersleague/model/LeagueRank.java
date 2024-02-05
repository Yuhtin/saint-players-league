package com.yuhtin.quotes.saint.playersleague.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LeagueRank {

    private final int id;
    private final String displayName;
    private final String prefix;
    private final int pointsNeeded;
    private final String luckpermsGroup;

    private final List<String> commandsRewards;

}
