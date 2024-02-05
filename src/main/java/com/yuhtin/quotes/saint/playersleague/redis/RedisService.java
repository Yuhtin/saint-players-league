package com.yuhtin.quotes.saint.playersleague.redis;

import com.yuhtin.quotes.saint.playersleague.redis.factory.RedisDAOFactory;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.io.Closeable;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

public class RedisService implements Closeable {

    @Getter private final JedisPool jedis;
    private final RedisDAOFactory daoFactory = new RedisDAOFactory(this);

    public RedisService(String host, int port, String password) {
        this.jedis = new JedisPool(defaultConfig(), host, port, 2000, password);
    }

    public RedisService(@NonNull URI uri, JedisPoolConfig config) {
        this.jedis = new JedisPool(config != null ? config : defaultConfig(), uri);
    }

    public boolean isConnected() {
        return (jedis != null && !(jedis.isClosed()));
    }

    public void disconnect() {
        if (!isConnected()) return;

        dao().clear();
        jedis.destroy();
    }

    public void createChannel(@NonNull String channelName, @NonNull JedisPubSub listener) {
        try (Jedis resource = jedis.getResource()) {
            CompletableFuture.runAsync(() -> resource.subscribe(listener, channelName));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private GenericObjectPoolConfig<Jedis> defaultConfig() {
        GenericObjectPoolConfig<Jedis> config = new GenericObjectPoolConfig<>();

        config.setMaxTotal(50);
        config.setMaxIdle(50);
        config.setMinIdle(8);
        config.setTestWhileIdle(true);

        return config;
    }

    public RedisDAOFactory dao() {
        return this.daoFactory;
    }

    @Override
    public void close() {
        disconnect();
    }

}
