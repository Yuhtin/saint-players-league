package com.yuhtin.quotes.saint.playersleague.redis;

import com.google.gson.Gson;
import com.yuhtin.quotes.saint.playersleague.redis.callback.CacheCallback;
import com.yuhtin.quotes.saint.playersleague.redis.dao.RedisDAO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.io.Closeable;
import java.util.Collection;
import java.util.Set;

@AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_ = @NonNull)
public class RedisCache<K, V> implements Closeable {

    private final RedisService redisService;
    @Getter private final RedisDAO<V> dao;

    public static <K, V> RedisCache<K, V> newCache(
            @NonNull String key, @NonNull RedisService redisService, @NonNull Class<V> clazz, Gson gson) {
        RedisDAO<V> dao = redisService.dao().register(key, clazz, gson);
        return new RedisCache<>(redisService, dao);
    }

    public static <K, V> RedisCache<K, V> newCache(
            @NonNull String key, @NonNull RedisService redisService, @NonNull Class<V> clazz) {
        RedisDAO<V> dao = redisService.dao().register(key, clazz);
        return new RedisCache<>(redisService, dao);
    }

    public void insert(K key, V value) {
        this.dao.insert(String.valueOf(key), value);
    }

    public void invalidate(K key) {
        this.dao.invalidate(String.valueOf(key));
    }

    public void invalidateSilent(K key) {
        this.dao.invalidateSilent(String.valueOf(key));
    }

    public boolean contains(K key) {
        return this.dao.contains(String.valueOf(key));
    }

    public V get(K key) {
        return this.dao.get(String.valueOf(key));
    }

    public CacheCallback<V> getApply(K key) {
        return this.dao.getApply(String.valueOf(key));
    }

    public void clear() {
        this.dao.keys().forEach(dao::invalidate);
    }

    public void forceClear() {
        this.dao.keys().forEach(dao::invalidateSilent);
    }

    public Collection<V> values() {
        return this.dao.values();
    }

    public Set<String> keys() {
        return this.dao.keys();
    }

    public Set<Pair<K, V>> entrySet() {
        return this.dao.entrySet();
    }

    public RedisDAO<V> redis() {
        return this.dao;
    }

    @Override
    public void close() {
        redisService.dao().unregister(dao.getTable());
    }

}
