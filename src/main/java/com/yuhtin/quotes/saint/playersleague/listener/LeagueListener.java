package com.yuhtin.quotes.saint.playersleague.listener;

import com.yuhtin.quotes.saint.playersleague.PlayersLeaguePlugin;
import com.yuhtin.quotes.saint.playersleague.event.RankChangeEvent;
import com.yuhtin.quotes.saint.playersleague.event.UserPointsChangedEvent;
import com.yuhtin.quotes.saint.playersleague.model.LeagueRank;
import com.yuhtin.quotes.saint.playersleague.util.DiscordWebhook;
import lombok.AllArgsConstructor;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;

@AllArgsConstructor
public class LeagueListener implements TerminableModule {

    private final PlayersLeaguePlugin instance;

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {

        Events.subscribe(PlayerJoinEvent.class)
                .handler(event -> instance.getController().retrieve(event.getPlayer().getName()).ifPresent(leagueUser -> {
                    for (String s : leagueUser.getCommandsNotExecuted()) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
                    }

                    leagueUser.getCommandsNotExecuted().clear();
                })).bindWith(consumer);

        Events.subscribe(UserPointsChangedEvent.class, EventPriority.MONITOR)
                .handler(event -> {
                    LeagueRank newRank = instance.getRankCache().getByPoints(event.getNewPoints());
                    LeagueRank oldRank = instance.getRankCache().getByPoints(event.getOldPoints());

                    sendAnnounce(event.getUser().getUsername(), event.getNewPoints() - event.getOldPoints(), event.getNewPoints());

                    if (newRank == oldRank) return;

                    Events.call(new RankChangeEvent(event.getPlayer(), event.getUser(), oldRank, newRank));
                }).bindWith(consumer);

        Events.subscribe(RankChangeEvent.class, EventPriority.MONITOR)
                .handler(event -> {
                    LeagueRank lastRank = event.getLastRank();
                    LeagueRank newRank = event.getNewRank();

                    event.getUser().setRankId(newRank.getId());
                    event.getUser().setRankPrefix(newRank.getPrefix());

                    String username = event.getUser().getUsername();

                    String removeGroupCommand = instance.getConfig().getString("remove-group-command")
                            .replace("%player%", username)
                            .replace("%group%", lastRank.getLuckpermsGroup());

                    String addGroupCommand = instance.getConfig().getString("add-group-command")
                            .replace("%player%", username)
                            .replace("%group%", newRank.getLuckpermsGroup());

                    if (event.getPlayer() != null) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), removeGroupCommand);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), addGroupCommand);
                    } else {
                        event.getUser().getCommandsNotExecuted().add(removeGroupCommand);
                        event.getUser().getCommandsNotExecuted().add(addGroupCommand);
                    }

                    // dont give rewards if it was a downgrade
                    if (lastRank.getId() > newRank.getId()) return;

                    newRank.getCommandsRewards().forEach(command -> {
                        String formattedCommand = command.replace("%player%", username);

                        if (event.getPlayer() != null) Bukkit.dispatchCommand(event.getPlayer(), formattedCommand);
                        else event.getUser().getCommandsNotExecuted().add(formattedCommand);
                    });
                }).bindWith(consumer);

    }

    private void sendAnnounce(String username, int points, int totalPoints) {
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
