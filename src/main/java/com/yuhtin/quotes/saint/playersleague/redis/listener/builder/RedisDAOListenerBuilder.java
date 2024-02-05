package com.yuhtin.quotes.saint.playersleague.redis.listener.builder;

import com.yuhtin.quotes.saint.playersleague.redis.dao.RedisDAO;
import com.yuhtin.quotes.saint.playersleague.redis.listener.RedisListener;
import com.yuhtin.quotes.saint.playersleague.redis.listener.type.RedisDAOListenerType;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(
        access = AccessLevel.PRIVATE,
        onConstructor_ = {@NonNull})
public class RedisDAOListenerBuilder {

    private final RedisDAOListenerType type;

    private final RedisListener<?> action;

    public static RedisDAOListenerBuilder newBuilder(RedisDAOListenerType type, RedisListener<?> listener) {
        return new RedisDAOListenerBuilder(type, listener);
    }

    public void register(RedisDAO<?> dao) {
        dao.registerListener(type, action);
    }

}
