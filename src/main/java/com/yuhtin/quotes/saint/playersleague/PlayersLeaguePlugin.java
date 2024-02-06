package com.yuhtin.quotes.saint.playersleague;

import com.henryfabio.minecraft.inventoryapi.manager.InventoryManager;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.yuhtin.quotes.saint.playersleague.cache.RankCache;
import com.yuhtin.quotes.saint.playersleague.command.PointsCommand;
import com.yuhtin.quotes.saint.playersleague.controller.UserController;
import com.yuhtin.quotes.saint.playersleague.hook.HookModule;
import com.yuhtin.quotes.saint.playersleague.listener.LeagueListener;
import com.yuhtin.quotes.saint.playersleague.module.RankingModule;
import com.yuhtin.quotes.saint.playersleague.placeholder.RankingPlaceholder;
import com.yuhtin.quotes.saint.playersleague.placeholder.UserPlaceholder;
import com.yuhtin.quotes.saint.playersleague.redis.RedisService;
import com.yuhtin.quotes.saint.playersleague.repository.SQLProvider;
import lombok.Getter;
import me.lucko.helper.plugin.ExtendedJavaPlugin;

@Getter
public class PlayersLeaguePlugin extends ExtendedJavaPlugin {

    private RedisService redisService;
    private SQLExecutor sqlExecutor;

    private RankCache rankCache;
    private UserController controller;

    @Override
    protected void load() {
        saveDefaultConfig();
        loadRedis();
        loadSQL();
    }

    @Override
    protected void enable() {
        InventoryManager.enable(this);

        rankCache = new RankCache(getConfig(), redisService);
        controller = new UserController(redisService, sqlExecutor);

        bindModule(new HookModule(this));
        bindModule(new PointsCommand(this));
        bindModule(new LeagueListener(this));
        bindModule(new RankingModule(this));

        new UserPlaceholder().register();
        new RankingPlaceholder().register();

        getLogger().info("Plugin ligado!");
    }

    private void loadRedis() {
        String host = getConfig().getString("redis.host");
        int port = getConfig().getInt("redis.port");
        String password = getConfig().getString("redis.password");

        this.redisService = new RedisService(host, port, password);
    }

    private void loadSQL() {
        SQLProvider sqlProvider = SQLProvider.of(this);
        sqlExecutor = new SQLExecutor(sqlProvider.setup(null));
    }

    public static PlayersLeaguePlugin getInstance() {
        return getPlugin(PlayersLeaguePlugin.class);
    }

}
