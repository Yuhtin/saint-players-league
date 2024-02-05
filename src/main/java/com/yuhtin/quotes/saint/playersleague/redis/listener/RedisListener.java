package com.yuhtin.quotes.saint.playersleague.redis.listener;

import com.yuhtin.quotes.saint.playersleague.redis.callback.CacheCallback;
import org.checkerframework.checker.nullness.qual.NonNull;

@SuppressWarnings("unchecked")
public interface RedisListener<V> {

    /**
     * @param key used to cached information
     * @param callback value used on listener
     * @apiNote callback can be null if used in a remove for example, not needing to return a value.
     */
    void onListener(@NonNull String key, @NonNull CacheCallback<V> callback);

    /**
     * @param key used to cached information on Redis
     * @param callback value used on listener
     */
    default void call(@NonNull String key, CacheCallback<?> callback) {
        onListener(key, (CacheCallback<V>) callback);
    }
}
