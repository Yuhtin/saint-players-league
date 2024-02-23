package com.yuhtin.quotes.saint.playersleague.model;

import com.yuhtin.quotes.saint.playersleague.util.DateFormatUtil;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */

@Data
@Builder
public class LeagueEvent {

    @Builder.Default
    private final String id = UUID.randomUUID().toString().split("-")[0];

    private final String name;

    private final String playerName;
    private final LeagueEventType leagueEventType;
    private final int points;

    @Builder.Default
    private final long timestamp = System.currentTimeMillis();

    public String getFormattedDate() {
        return DateFormatUtil.of(timestamp);
    }

}

