package com.yuhtin.quotes.saint.playersleague.repository.adapters;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;

public class LeagueUsernameAdapter implements SQLResultAdapter<String> {

    @Override
    public String adaptResult(SimpleResultSet resultSet) {
        return resultSet.get("username");
    }

}
