package com.yuhtin.quotes.saint.playersleague.repository.adapters;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import com.yuhtin.quotes.saint.playersleague.model.LeagueEvent;
import com.yuhtin.quotes.saint.playersleague.model.LeagueEventType;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */
public class LeagueEventAdapter implements SQLResultAdapter<LeagueEvent> {

    @Override
    public LeagueEvent adaptResult(SimpleResultSet resultSet) {
        return LeagueEvent.builder()
                .id(resultSet.get("id"))
                .name(resultSet.get("name"))
                .playerName(resultSet.get("playerName"))
                .leagueEventType(LeagueEventType.valueOf(resultSet.get("event_type")))
                .points(resultSet.get("points"))
                .timestamp(resultSet.get("timestamp"))
                .build();
    }
}
