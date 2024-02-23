package com.yuhtin.quotes.saint.playersleague.cache;

import com.yuhtin.quotes.saint.playersleague.PlayersLeaguePlugin;
import com.yuhtin.quotes.saint.playersleague.view.HistoricView;
import com.yuhtin.quotes.saint.playersleague.view.LeagueView;
import com.yuhtin.quotes.saint.playersleague.view.RankingView;
import lombok.Getter;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */
@Getter
public class ViewCache {

    private final PlayersLeaguePlugin plugin;
    private final HistoricView historicView;
    private final LeagueView leagueView;
    private final RankingView rankingView;

    public ViewCache(PlayersLeaguePlugin plugin) {
        this.plugin = plugin;
        this.leagueView = new LeagueView(this).init();
        this.historicView = new HistoricView(this).init();
        this.rankingView = new RankingView(this).init();
    }

}
