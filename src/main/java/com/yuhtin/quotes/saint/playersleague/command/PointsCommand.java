package com.yuhtin.quotes.saint.playersleague.command;

import com.yuhtin.quotes.saint.playersleague.PlayersLeaguePlugin;
import com.yuhtin.quotes.saint.playersleague.model.LeagueEvent;
import com.yuhtin.quotes.saint.playersleague.model.LeagueEventType;
import lombok.AllArgsConstructor;
import me.lucko.helper.Commands;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class PointsCommand implements TerminableModule {

    private final PlayersLeaguePlugin instance;

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        Commands.create()
                .assertPlayer()
                .handler(context -> instance.getViewCache().getLeagueView().openInventory(context.sender()))
                .registerAndBind(consumer, "ligaplayer");

        Commands.create()
                .assertPermission("playersleague.admin")
                .assertUsage("<add|remove> <player> <amount> <motivo>")
                .handler(context -> {
                    String function = context.arg(0).parseOrFail(String.class);
                    String username = context.arg(1).parseOrFail(String.class);
                    int points = Math.max(0, context.arg(2).parseOrFail(Integer.class));
                    int total = function.equalsIgnoreCase("remove") ? points * -1 : points;

                    StringBuilder builder = new StringBuilder();
                    for (String arg : context.args().subList(3, context.args().size())) {
                        builder.append(arg).append(" ");
                    }

                    String motive = builder.toString().trim();

                    instance.getController().findIfPresent(username).ifPresentOrElse(user -> {
                        user.setPoints(Math.max(0, user.getPoints() + total));

                        LeagueEvent leagueEvent = LeagueEvent.builder()
                                .name(motive)
                                .playerName(user.getUsername())
                                .leagueEventType(LeagueEventType.DEFAULT)
                                .points(total)
                                .build();

                        instance.getController().getEventRepository().insert(leagueEvent);
                        context.reply("&aPontos de &f" + user.getUsername() + " &aalterados para &f" + user.getPoints() + "&a.");
                    }, () -> {
                        context.reply("&cUsuário não encontrado.");
                    });
                }).registerAndBind(consumer, "ligapontosadmin");
    }

}
