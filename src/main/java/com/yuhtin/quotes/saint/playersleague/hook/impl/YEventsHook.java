package com.yuhtin.quotes.saint.playersleague.hook.impl;

import com.ystoreplugins.yeventos.event.EventType;
import com.ystoreplugins.yeventos.event.PlayerWinEventEvent;
import com.yuhtin.quotes.saint.playersleague.PlayersLeaguePlugin;
import com.yuhtin.quotes.saint.playersleague.hook.LeagueEventHook;
import com.yuhtin.quotes.saint.playersleague.model.LeagueEvent;
import com.yuhtin.quotes.saint.playersleague.model.LeagueEventType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class YEventsHook extends LeagueEventHook {

    private final PlayersLeaguePlugin instance;

    @Override
    public String pluginName() {
        return "yEventos";
    }

    @Override
    public void setup(@Nonnull TerminableConsumer consumer) {

        Events.subscribe(PlayerWinEventEvent.class)
                .handler(event -> {
                    Player player = event.getPlayer();
                    EventType eventType = event.getEventType();

                    String path = "reward-per-event.yEvents." + eventType.name().toUpperCase();

                    int points = instance.getConfig().getInt(path + ".points", -1);
                    if (points == -1) {
                        instance.getLogger().severe("[yEventos] Não foi possível encontrar a quantidade de pontos para o evento " + eventType.name().toUpperCase() + " (" + path + ")");
                        return;
                    }

                    String eventName = instance.getConfig().getString(path + ".name", eventType.name());

                    LeagueEvent leagueEvent = LeagueEvent.builder()
                            .name(eventName)
                            .playerName(player.getName())
                            .leagueEventType(LeagueEventType.WIN_EVENTS)
                            .points(points)
                            .build();

                    instance.getController().getEventRepository().insert(leagueEvent);

                    instance.getController().addPoints(player.getName(), points);
                    instance.getLogger().info("[yEventos] [" + eventName + "] Vitória de " + player.getName() + " (+ " + points + " pontos)");
                }).bindWith(consumer);

    }

}
