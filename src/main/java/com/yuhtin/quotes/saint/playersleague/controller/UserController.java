package com.yuhtin.quotes.saint.playersleague.controller;

import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.yuhtin.quotes.saint.playersleague.cache.UserCache;
import com.yuhtin.quotes.saint.playersleague.model.LeagueUser;
import com.yuhtin.quotes.saint.playersleague.redis.RedisService;
import com.yuhtin.quotes.saint.playersleague.redis.callback.CacheCallback;
import com.yuhtin.quotes.saint.playersleague.repository.repository.UserRepository;
import lombok.Getter;
import me.lucko.helper.Schedulers;
import me.lucko.helper.promise.Promise;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class UserController {

    private final UserCache cache;
    private final UserRepository repository;

    private List<String> ranking = new ArrayList<>();

    public UserController(RedisService redisService, SQLExecutor executor) {
        this.cache = new UserCache(redisService);
        this.repository = new UserRepository(executor);
        this.repository.createTable();
    }

    public LeagueUser getByPosition(int position) {
        if (ranking.size() <= position) return null;

        String tag = ranking.get(position);
        LeagueUser leagueUser = new LeagueUser(tag);
        leagueUser.setPointsSilent(getPoints(tag));

        return leagueUser;
    }

    public Promise<Void> refresh() {
        return Schedulers.sync().run(() -> {
            ranking = new ArrayList<>(repository.orderByPoints());
        });
    }

    public int getPoints(String username) {
        CacheCallback<LeagueUser> ifPresent = findIfPresent(username);
        return ifPresent.isPresent() ? ifPresent.getUncachedValue().getPoints() : 0;
    }

    public void addPoints(String username, int points) {
        findIfPresent(username).ifPresent(user -> user.addPoints(points));
    }

    public void update(@NotNull LeagueUser user) {
        cache.insert(user);
    }

    @NotNull
    public CacheCallback<LeagueUser> retrieve(@NotNull String username) {
        CacheCallback<LeagueUser> value = findIfPresent(username);
        if (value.isPresent()) return value;

        LeagueUser user = new LeagueUser(username);
        cache.insert(user);
        repository.insert(user);

        return new CacheCallback<>(username, user, cache.dao());
    }

    @NotNull
    public CacheCallback<LeagueUser> findIfPresent(String username) {
        CacheCallback<LeagueUser> user = cache.get(username);
        if (user != null) return user;

        LeagueUser userLoaded = repository.find(username);
        if (userLoaded != null) {
            cache.insert(userLoaded);
        }

        return userLoaded != null
                ? new CacheCallback<>(userLoaded.getUsername(), userLoaded, cache.dao())
                : CacheCallback.empty();
    }

}
