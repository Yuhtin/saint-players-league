package com.yuhtin.quotes.saint.playersleague.hook.impl;

import com.yuhtin.quotes.saint.playersleague.PlayersLeaguePlugin;
import com.yuhtin.quotes.saint.playersleague.hook.LeagueEventHook;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.lucko.helper.Commands;
import me.lucko.helper.terminable.TerminableConsumer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class DragonSlayerHook extends LeagueEventHook {

    private final PlayersLeaguePlugin instance;

    @Override
    public String pluginName() {
        return "DragonSlayer";
    }

    @Override
    public void setup(@Nonnull TerminableConsumer consumer) {

        Commands.create()
                .assertConsole()
                .assertUsage("<slayer>")
                .handler(context -> {
                    Player player = context.arg(0).parseOrFail(Player.class);

                    String path = "reward-per-event.DragonSlayer";

                    int points = instance.getConfig().getInt(path + ".points", -1);
                    if (points == -1) {
                        instance.getLogger().severe("[DragonSlayer] Não foi possível encontrar a quantidade de pontos para o evento (" + path + ")");
                        return;
                    }

//                    String eventName = instance.getConfig().getString(path + ".name", "DragonSlayer");

                    instance.getController().addPoints(player.getName(), points);
                    instance.getLogger().info("[DragonSlayer] Vitória de " + player.getName() + " (+ " + points + " pontos)");
                }).registerAndBind(consumer, "dragonslayerkill2");

    }
}
