/*
 * Copyright (c) 2022.
 *
 * Developers: Pedro Aguiar, Gabriel Santos
 *
 * Force, Inc (github.com/rede-force)
 */

package com.yuhtin.quotes.saint.playersleague.redis.callback;

import com.yuhtin.quotes.saint.playersleague.redis.dao.RedisDAO;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * The type Cache callback used to manage return caching
 *
 * @param <V> the value type
 */
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor
public class CacheCallback<V> {

    private final String key;

    private final V value;

    private RedisDAO<V> redisDao;

    public static <V> CacheCallback<V> empty() {
        return new CacheCallback<>(null, null);
    }

    /**
     * @return value will be discarded at the end of the method
     * @apiNote If you are using redis:
     *     <p>Don't update the mem value using this method
     */
    public V getUncachedValue() {
        return value;
    }

    public boolean isPresent() {
        return value != null;
    }

    public boolean isEmpty() {
        return value == null;
    }

    public void ifPresent(Consumer<V> action) {
        if (isEmpty()) return;

        apply(action);
    }

    public void ifPresentOrElse(Consumer<V> action, Runnable orElse) {
        if (isPresent()) {
            apply(action);
        } else {
            orElse.run();
        }
    }

    public <U> U map(@NonNull Function<V, U> mapper, U defaultValue) {
        return isEmpty() ? defaultValue : mapper.apply(value);
    }

    @NonNull
    public V orElse(@NonNull V defaultValue) {
        return isPresent() ? value : defaultValue;
    }

    public Stream<V> stream() {
        return isPresent() ? Stream.of(value) : Stream.empty();
    }

    /**
     * @param action will update current value on cache
     */
    private void apply(Consumer<V> action) {
        action.accept(value);

        if (redisDao != null) {
            redisDao.insertSilent(key, value);
        }
    }
}
