package com.yuhtin.quotes.saint.playersleague.cache;

import com.yuhtin.quotes.saint.playersleague.model.LeagueUser;
import com.yuhtin.quotes.saint.playersleague.redis.RedisCache;
import com.yuhtin.quotes.saint.playersleague.redis.RedisService;
import com.yuhtin.quotes.saint.playersleague.redis.callback.CacheCallback;
import com.yuhtin.quotes.saint.playersleague.redis.dao.RedisDAO;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class UserCache {

    private final RedisCache<String, LeagueUser> cache;

    public UserCache(RedisService redisService) {
        this.cache = RedisCache.newCache("players_league:users", redisService, LeagueUser.class);
    }

    public void insert(@NotNull LeagueUser user) {
        this.cache.insert(user.getUsername().toLowerCase(), user);
    }

    @Nullable
    public CacheCallback<LeagueUser> get(String username) {
        return this.cache.getApply(username.toLowerCase());
    }

    public void invalidate(@NotNull String username) {
        this.cache.invalidate(username.toLowerCase());
    }

    public RedisDAO<LeagueUser> dao() {
        return cache.getDao();
    }

}
