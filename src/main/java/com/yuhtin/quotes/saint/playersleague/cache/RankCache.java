package com.yuhtin.quotes.saint.playersleague.cache;

import com.yuhtin.quotes.saint.playersleague.model.LeagueRank;
import com.yuhtin.quotes.saint.playersleague.redis.RedisCache;
import com.yuhtin.quotes.saint.playersleague.redis.RedisService;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

public class RankCache {

    private final RedisCache<Integer, LeagueRank> cache;

    public RankCache(FileConfiguration config, RedisService service) {
        this.cache = RedisCache.newCache("players_league:ranks", service, LeagueRank.class);
        loadConfig(config);
    }

    private void loadConfig(FileConfiguration config) {
        config.getConfigurationSection("ranks").getKeys(false).forEach(key -> {
            int id = Integer.parseInt(key);

            String displayName = config.getString("ranks." + key + ".display_name");
            String prefix = config.getString("ranks." + key + ".prefix");
            String luckpermsGroup = config.getString("ranks." + key + ".group");

            int pointsNeeded = config.getInt("ranks." + key + ".points_needed");

            List<String> commandsRewards = config.getStringList("ranks." + key + ".commands");

            insert(LeagueRank.builder()
                    .id(id)
                    .displayName(displayName)
                    .prefix(prefix)
                    .pointsNeeded(pointsNeeded)
                    .luckpermsGroup(luckpermsGroup)
                    .commandsRewards(commandsRewards)
                    .build());
        });
    }

    public void insert(LeagueRank rank) {
        this.cache.insert(rank.getId(), rank);
    }

    public LeagueRank get(int id) {
        return this.cache.get(id);
    }

    @NotNull
    public LeagueRank getByPoints(int points) {
        return this.cache.values().stream()
                .filter(rank -> rank.getPointsNeeded() <= points)
                .max(Comparator.comparingInt(LeagueRank::getPointsNeeded))
                .orElse(this.cache.get(1));
    }

}
