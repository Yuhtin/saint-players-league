package com.yuhtin.quotes.saint.playersleague.module;

import com.yuhtin.quotes.saint.playersleague.PlayersLeaguePlugin;
import com.yuhtin.quotes.saint.playersleague.model.LeagueUser;
import com.yuhtin.quotes.saint.playersleague.repository.repository.UserRepository;
import lombok.AllArgsConstructor;
import me.lucko.helper.Schedulers;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.Set;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */
@AllArgsConstructor
public class AutoRewardModule implements TerminableModule {

    private final PlayersLeaguePlugin instance;

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {

        long initialTime = instance.getConfig().getLong("initial-time.MENSAL", -1);
        if (initialTime == -1) {
            instance.getConfig().set("initial-time.MENSAL", System.currentTimeMillis());
            instance.saveConfig();

            initialTime = System.currentTimeMillis();
        }

        long resetTime = instance.getConfig().getLong("reset-time.MENSAL", -1);
        if (resetTime == -1) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(initialTime);
            calendar.add(Calendar.MONTH, 1);

            instance.getConfig().set("reset-time.MENSAL", calendar.getTimeInMillis());
            instance.saveConfig();
        }

        Schedulers.sync().runRepeating(runnable -> {
            if (instance.getConfig().getInt("reset-time.MENSAL", -1) > System.currentTimeMillis()) return;
            if (!runReward()) return;

            saveLog();
            resetRepository();
        }, 20L, 1200L).bindWith(consumer);
    }

    private boolean runReward() {
        UserRepository repository = instance.getController().getRepository();

        Set<LeagueUser> leaguePlayers = repository.orderByPoints(3);
        if (leaguePlayers.isEmpty()) {
            this.instance.getLogger().severe("[MENSAL] Não foi possível encontrar nenhum player para ser recompensado!");
            return false;
        }

        int i = 1;
        for (LeagueUser player : leaguePlayers) {
            this.instance.getLogger().info("[MENSAL] Recompensando o player " + player + " com o " + i + "º lugar!");

            for (String command : this.instance.getConfig().getStringList("auto-reward." + i)) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                        .replace("$player", player.getUsername()));
            }
            i++;
        }
        this.instance.getLogger().info("[MENSAL] Recompensas automáticas executadas com sucesso!");
        return true;
    }

    private void saveLog() {
        UserRepository repository = instance.getController().getRepository();
        File file = new File(instance.getDataFolder(), getLogFileLocation());
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception exception) {
                exception.printStackTrace();
                return;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            writer.write("Players com mais pontos no ranking mensal:\n\n");
            for (LeagueUser player : repository.orderByPoints(-1)) {
                writer.write(player.getUsername() + " - " + player.getRankPrefix() + " - " + player.getPoints() + "\n");
            }

            writer.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    private String getLogFileLocation() {
        Calendar calendar = Calendar.getInstance();

        String formatedDate = calendar.get(Calendar.DAY_OF_MONTH)
                + "-" + (calendar.get(Calendar.MONTH) + 1)
                + "-" + calendar.get(Calendar.YEAR);

        return "past-leagues/mensal/" + formatedDate + ".txt";
    }

    private void resetRepository() {
        instance.getController().resetPoints();

        long value = System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(value);
        calendar.add(Calendar.MONTH, 1);

        instance.getConfig().set("initial-time.MENSAL", value);
        instance.getConfig().set("reset-time.MENSAL", calendar.getTimeInMillis());
        instance.saveConfig();

        this.instance.getLogger().info("Sistema MENSAL reiniciado com sucesso!");
    }
}
