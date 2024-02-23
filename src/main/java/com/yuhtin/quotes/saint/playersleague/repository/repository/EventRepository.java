package com.yuhtin.quotes.saint.playersleague.repository.repository;

import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.yuhtin.quotes.saint.playersleague.PlayersLeaguePlugin;
import com.yuhtin.quotes.saint.playersleague.model.LeagueEvent;
import com.yuhtin.quotes.saint.playersleague.repository.adapters.LeagueEventAdapter;
import com.yuhtin.quotes.saint.playersleague.util.DiscordWebhook;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lucko.helper.Schedulers;

import java.awt.*;
import java.io.IOException;
import java.util.Set;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */
@RequiredArgsConstructor
public final class EventRepository {

    private static final String TABLE = "playerleague_event_data";

    @Getter
    private final SQLExecutor sqlExecutor;

    public void createTable() {
        sqlExecutor.updateQuery("CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                "id CHAR(8) NOT NULL PRIMARY KEY," +
                "name CHAR(36) NOT NULL," +
                "playerName CHAR(36) NOT NULL," +
                "event_type CHAR(36) NOT NULL," +
                "points INT NOT NULL," +
                "timestamp BIGINT NOT NULL" +
                ");"
        );
    }

    public void recreateTable() {
        sqlExecutor.updateQuery("DROP TABLE IF EXISTS " + TABLE);
        createTable();
    }

    public Set<LeagueEvent> groupByPlayer(String playerName) {
        return sqlExecutor.resultManyQuery(
                "SELECT * FROM " + TABLE + " WHERE playerName = '" + playerName + "'",
                statement -> {
                },
                LeagueEventAdapter.class
        );
    }

    public void insert(LeagueEvent leagueEvent) {
        this.sqlExecutor.updateQuery(
                String.format("REPLACE INTO %s VALUES(?,?,?,?,?,?)", TABLE),
                statement -> {
                    statement.set(1, leagueEvent.getId());
                    statement.set(2, leagueEvent.getName());
                    statement.set(3, leagueEvent.getPlayerName());
                    statement.set(4, leagueEvent.getLeagueEventType().name());
                    statement.set(5, leagueEvent.getPoints());
                    statement.set(6, leagueEvent.getTimestamp());
                }
        );

        Schedulers.async().runLater(() -> {
            int points = PlayersLeaguePlugin.getInstance().getController().getPoints(leagueEvent.getPlayerName());
            sendAnnounce(leagueEvent.getPlayerName(), leagueEvent.getPoints(), points);
        }, 5L);
    }

    private void sendAnnounce(String username, int points, int totalPoints) {
        PlayersLeaguePlugin instance = PlayersLeaguePlugin.getInstance();
        DiscordWebhook discordWebhook = new DiscordWebhook(instance.getConfig().getString("discord-webhook-link"));
        DiscordWebhook.EmbedObject embedObject = new DiscordWebhook.EmbedObject();

        embedObject.setTitle(instance.getConfig().getString("webhook-title").replace("%player%", username));
        embedObject.setDescription(instance.getConfig().getString("webhook-description")
                .replace("%player%", username)
                .replace("%points%", points > 0 ? "+" + points : String.valueOf(points))
                .replace("%total%", String.valueOf(totalPoints)));

        embedObject.setFooter(instance.getConfig().getString("webhook-footer"), instance.getConfig().getString("webhook-footer-icon"));
        embedObject.setColor(Color.getColor(instance.getConfig().getString("webhook-color")));

        discordWebhook.addEmbed(embedObject);

        try {
            discordWebhook.execute();
        } catch (IOException exception) {
            exception.printStackTrace();
            instance.getLogger().severe("[Webhook] An error occurred while sending discord webhook message.");
        }

    }

}
