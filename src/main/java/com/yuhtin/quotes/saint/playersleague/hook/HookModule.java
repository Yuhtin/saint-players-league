package com.yuhtin.quotes.saint.playersleague.hook;

import com.google.common.reflect.ClassPath;
import com.yuhtin.quotes.saint.leagues.LeaguesPlugin;
import com.yuhtin.quotes.saint.playersleague.PlayersLeaguePlugin;
import lombok.AllArgsConstructor;
import lombok.val;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */
@AllArgsConstructor
public class HookModule implements TerminableModule {

    private final PlayersLeaguePlugin instance;

    @Override
    public void setup(@Nonnull TerminableConsumer consumer) {
        PluginManager pluginManager = Bukkit.getPluginManager();

        // get all classes that extends LeagueEventHook
        // and register them as a listener

        ClassPath classPath;
        try {
            classPath = ClassPath.from(getClass().getClassLoader());
        } catch (IOException exception) {
            instance.getLogger().severe("ClassPath could not be instantiated");
            return;
        }

        for (val info : classPath.getTopLevelClassesRecursive("com.yuhtin.quotes.saint.leagues.hook.impl")) {
            try {
                Class<?> name = Class.forName(info.getName());
                if (name.newInstance() instanceof LeagueEventHook) {
                    LeagueEventHook hook = (LeagueEventHook) name.getDeclaredConstructor(PlayersLeaguePlugin.class).newInstance(instance);
                    instance.getLogger().info("[" + hook.pluginName() + "] Iniciando hook...");

                    if (!pluginManager.isPluginEnabled(hook.pluginName())) {
                        instance.getLogger().warning("[" + hook.pluginName() + "] Dependencia " + hook.pluginName() + " não encontrada");
                        instance.getLogger().warning("[" + hook.pluginName() + "] Desabilitando hook deste plugin!");

                        continue;
                    }

                    instance.bindModule(hook);
                    instance.getLogger().info("[" + hook.pluginName() + "] Hook iniciado com sucesso!");
                } else throw new InstantiationException();
            } catch (Exception exception) {
                exception.printStackTrace();
                instance.getLogger().severe("The " + info.getName() + " class could not be instantiated");
            }
        }

    }
}
