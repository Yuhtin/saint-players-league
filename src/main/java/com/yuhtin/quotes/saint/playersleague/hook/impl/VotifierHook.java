package com.yuhtin.quotes.saint.playersleague.hook.impl;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import com.yuhtin.quotes.saint.playersleague.PlayersLeaguePlugin;
import com.yuhtin.quotes.saint.playersleague.hook.LeagueEventHook;
import com.yuhtin.quotes.saint.playersleague.model.LeagueEvent;
import com.yuhtin.quotes.saint.playersleague.model.LeagueEventType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class VotifierHook extends LeagueEventHook {

    private final PlayersLeaguePlugin instance;

    @Override
    public String pluginName() {
        return "Votifier";
    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {

        Events.subscribe(VotifierEvent.class)
                .filter(event -> !event.getVote().getServiceName().isEmpty())
                .filter(event -> !event.getVote().getUsername().trim().isEmpty())
                .handler(event -> {
                    Vote vote = event.getVote();
                    String username = vote.getUsername().trim();

                    String path = "reward-per-event.Vote";

                    String name = instance.getConfig().getString(path + ".name");
                    int points = instance.getConfig().getInt(path + ".points");

                    LeagueEvent leagueEvent = LeagueEvent.builder()
                            .name(name)
                            .playerName(username)
                            .leagueEventType(LeagueEventType.DEFAULT)
                            .points(points)
                            .build();

                    instance.getController().getEventRepository().insert(leagueEvent);

                    instance.getController().addPoints(username, points);
                }).bindWith(consumer);

    }

}
