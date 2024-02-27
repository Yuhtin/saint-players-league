package com.yuhtin.quotes.saint.playersleague.repository.adapters;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import com.yuhtin.quotes.saint.playersleague.PlayersLeaguePlugin;
import com.yuhtin.quotes.saint.playersleague.model.LeagueRank;
import com.yuhtin.quotes.saint.playersleague.model.LeagueUser;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */
public class LeagueUserAdapter implements SQLResultAdapter<LeagueUser> {

    @Override
    public LeagueUser adaptResult(SimpleResultSet resultSet) {
        LeagueUser leagueUser = new LeagueUser(resultSet.get("username"));
        leagueUser.setPoints(resultSet.get("points"));
        leagueUser.setRankId(resultSet.get("rank_id"));

        LeagueRank rank = PlayersLeaguePlugin.getInstance().getRankCache().get(leagueUser.getRankId());
        leagueUser.setRankPrefix(rank.getPrefix());

        return leagueUser;
    }
}
