package com.yuhtin.quotes.saint.playersleague.redis.factory;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.Gson;
import com.yuhtin.quotes.saint.playersleague.redis.RedisService;
import com.yuhtin.quotes.saint.playersleague.redis.dao.RedisDAO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
public class RedisDAOFactory {

    private final Cache<String, RedisDAO<?>> cache = Caffeine.newBuilder().build();

    private final RedisService redisService;

    @SuppressWarnings("unchecked")
    public <T> RedisDAO<T> getDao(@NonNull String key) {
        Objects.requireNonNull(cache.getIfPresent(key), "Table doesn't exists.");

        return (RedisDAO<T>) cache.getIfPresent(key);
    }

    public void unregister(@NonNull String key) {
        cache.invalidate(key);
    }

    public <V> RedisDAO<V> register(@NonNull String key, @NonNull Class<V> clazz, @NonNull Gson gson) {
        RedisDAO<V> dao = new RedisDAO<>(key, clazz, redisService, gson);
        cache.put(key, dao);

        return dao;
    }

    public <V> RedisDAO<V> register(@NonNull String key, @NonNull Class<V> clazz) {
        RedisDAO<V> dao = new RedisDAO<>(key, clazz, redisService, new Gson());
        cache.put(key, dao);

        return dao;
    }

    public void clear() {
        cache.invalidateAll();
    }
}
