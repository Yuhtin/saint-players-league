package com.yuhtin.quotes.saint.playersleague.hook.impl;

import com.yuhtin.quotes.saint.playersleague.PlayersLeaguePlugin;
import com.yuhtin.quotes.saint.playersleague.hook.LeagueEventHook;
import com.yuhtin.quotes.saint.playersleague.model.LeagueEvent;
import com.yuhtin.quotes.saint.playersleague.model.LeagueEventType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.roinujnosde.titansbattle.events.PlayerWinEvent;
import me.roinujnosde.titansbattle.types.Warrior;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class TitansBattleHook extends LeagueEventHook {

    private final PlayersLeaguePlugin instance;

    @Override
    public String pluginName() {
        return "TitansBattle";
    }

    @Override
    public void setup(@Nonnull TerminableConsumer consumer) {

        Events.subscribe(PlayerWinEvent.class)
                .handler(event -> {
                    String eventName = event.getGame().getConfig().getName();
                    List<Warrior> players = event.getPlayers();

                    String path = "reward-per-event.TitansBattle." + eventName;

                    int points = instance.getConfig().getInt(path + ".points", -1);
                    if (points == -1) {
                        instance.getLogger().severe("[TitansBattle] Não foi possível encontrar a quantidade de pontos para o evento " + eventName + " (" + path + ")");
                        return;
                    }

                    String name = instance.getConfig().getString(path + ".name", "TitansBattle");

                    for (Warrior player : players) {
                        instance.getController().addPoints(player.getName(), points);

                        LeagueEvent leagueEvent = LeagueEvent.builder()
                                .name(name)
                                .playerName(player.getName())
                                .leagueEventType(LeagueEventType.WIN_CLAN_EVENTS)
                                .points(points)
                                .build();

                        instance.getController().getEventRepository().insert(leagueEvent);

                        instance.getLogger().info("[TitansBattle] Vitória de " + player.getName() + " (+ " + points + " pontos)");
                    }
                }).bindWith(consumer);

    }

}
