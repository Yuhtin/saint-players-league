package com.yuhtin.quotes.saint.playersleague.redis.dao;

import com.google.gson.Gson;
import com.yuhtin.quotes.saint.playersleague.redis.Pair;
import com.yuhtin.quotes.saint.playersleague.redis.RedisService;
import com.yuhtin.quotes.saint.playersleague.redis.callback.CacheCallback;
import com.yuhtin.quotes.saint.playersleague.redis.listener.RedisListener;
import com.yuhtin.quotes.saint.playersleague.redis.listener.type.RedisDAOListenerType;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RedisDAO<V> {

    @Getter private final String table;
    private final Class<V> clazz;
    private final RedisService service;
    private final Gson gson;

    private final EnumMap<RedisDAOListenerType, RedisListener<?>> listeners = new EnumMap<>(RedisDAOListenerType.class);

    /**
     * Register a listener to this DAO.
     *
     * @param type The type of listener to register.
     * @param listener The listener manager
     */
    public void registerListener(@NonNull RedisDAOListenerType type, RedisListener<?> listener) {
        listeners.put(type, listener);
    }

    public void insert(String key, V value) {
        insertSilent(key, value);

        CacheCallback<V> cacheCallback = new CacheCallback<V>(key, value, this);

        if (listeners.containsKey(RedisDAOListenerType.INSERT)) {
            listeners.get(RedisDAOListenerType.INSERT).call(key, cacheCallback);
        }

        if (listeners.containsKey(RedisDAOListenerType.UPDATE) && contains(key)) {
            listeners.get(RedisDAOListenerType.UPDATE).call(key, cacheCallback);
        }
    }

    public void insertSilent(String key, V value) {
        try (Jedis jedisPool = service.getJedis().getResource()) {
            Pipeline pipelined = jedisPool.pipelined();

            pipelined.hset(table, key, gson.toJson(value));
            pipelined.sync();
        }
    }

    public void invalidate(String key) {
        try (Jedis jedisPool = service.getJedis().getResource()) {
            Pipeline pipelined = jedisPool.pipelined();
            Response<String> response = pipelined.hget(table, key);

            pipelined.sync();

            if (response == null || response.get() == null) return;

            V value = gson.fromJson(response.get(), clazz);
            CacheCallback<V> cacheCallback = new CacheCallback<>(key, value, this);

            pipelined.hdel(table, key);
            pipelined.sync();

            if (listeners.containsKey(RedisDAOListenerType.INVALIDATE)) {
                listeners.get(RedisDAOListenerType.INVALIDATE).call(key, cacheCallback);
            }
        }
    }

    public void invalidateSilent(String key) {
        try (Jedis jedisPool = service.getJedis().getResource()) {
            Pipeline pipelined = jedisPool.pipelined();
            Response<Boolean> response = pipelined.hexists(table, key);

            pipelined.sync();

            if (response == null || !response.get()) return;

            pipelined.hdel(table, key);
            pipelined.sync();
        }
    }

    public CacheCallback<V> getApply(String key) {
        try (Jedis jedisPool = service.getJedis().getResource()) {
            Pipeline pipelined = jedisPool.pipelined();
            Response<String> response = pipelined.hget(table, key);

            pipelined.sync();

            if (response == null || response.get() == null) return new CacheCallback<>(key, null, this);

            return new CacheCallback<>(key, gson.fromJson(response.get(), clazz), this);
        }
    }

    public V get(String key) {
        try (Jedis jedisPool = service.getJedis().getResource()) {
            Pipeline pipelined = jedisPool.pipelined();
            Response<String> response = pipelined.hget(table, key);

            pipelined.sync();

            if (response == null || response.get() == null) return null;

            return gson.fromJson(response.get(), clazz);
        }
    }

    public boolean contains(String key) {
        try (Jedis jedisPool = service.getJedis().getResource()) {
            Pipeline pipelined = jedisPool.pipelined();
            Response<Boolean> response = pipelined.hexists(table, key);

            pipelined.sync();

            return response.get();
        }
    }

    public <T> Set<Pair<T, V>> entrySet() {
        try (Jedis jedisPool = service.getJedis().getResource()) {
            Pipeline pipelined = jedisPool.pipelined();
            Response<Map<String, String>> response = pipelined.hgetAll(table);

            pipelined.sync();

            return (response.get().entrySet().stream()
                    .map(entry -> Pair.of((T) entry.getKey(), gson.fromJson(entry.getValue(), clazz)))
                    .collect(Collectors.toSet()));
        }
    }

    public Set<V> values() {
        try (Jedis jedisPool = service.getJedis().getResource()) {
            Pipeline pipelined = jedisPool.pipelined();
            Response<Map<String, String>> response = pipelined.hgetAll(table);

            pipelined.sync();

            return (response.get().values().stream()
                    .map(value -> gson.fromJson(value, clazz))
                    .collect(Collectors.toSet()));
        }
    }

    public Set<String> keys() {
        try (Jedis jedisPool = service.getJedis().getResource()) {
            Pipeline pipelined = jedisPool.pipelined();
            Response<Set<String>> response = pipelined.hkeys(table);

            pipelined.sync();

            return (response.get());
        }
    }
}
