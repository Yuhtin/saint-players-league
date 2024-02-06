package com.yuhtin.quotes.saint.playersleague.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class Pair<K, V> {

    private final K key;
    private final V value;

}