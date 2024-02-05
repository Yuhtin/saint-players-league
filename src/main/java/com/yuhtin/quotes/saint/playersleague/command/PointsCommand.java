package com.yuhtin.quotes.saint.playersleague.command;

import com.yuhtin.quotes.saint.playersleague.PlayersLeaguePlugin;
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
                .assertPermission("playersleague.admin")
                .assertUsage("<add|remove|set> <player> <amount>")
                .handler(context -> {
                    String function = context.arg(0).parseOrFail(String.class);
                    String username = context.arg(1).parseOrFail(String.class);
                    int amount = context.arg(2).parseOrFail(Integer.class);
                    if (amount < 0) {
                        context.reply("&cA quantidade de pontos não pode ser negativa.");
                        return;
                    }

                    instance.getController().findIfPresent(username).ifPresentOrElse(user -> {
                        switch (function) {
                            case "add":
                                user.setPoints(Math.max(0, user.getPoints() + amount));
                                break;
                            case "remove":
                                user.setPoints(Math.max(0, user.getPoints() - amount));
                                break;
                            case "set":
                                user.setPoints(amount);
                                break;
                            default:
                                context.reply("&cFunção não encontrada.");
                        }

                        context.reply("&aPontos de &f" + user.getUsername() + " &aalterados para &f" + user.getPoints() + "&a.");
                    }, () -> {
                        context.reply("&cUsuário não encontrado.");
                    });
                }).registerAndBind(consumer, "pontos");
    }

}
