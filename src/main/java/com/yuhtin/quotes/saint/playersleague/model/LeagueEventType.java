package com.yuhtin.quotes.saint.playersleague.model;

import com.yuhtin.quotes.saint.playersleague.util.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */
@AllArgsConstructor
@Getter
public enum LeagueEventType {

    KILL_DRAGON(new ItemStack(Material.ENDER_DRAGON_SPAWN_EGG)),
    WIN_CLAN_EVENTS(new ItemBuilder("/texture/63b63f825a934d573bd1e974e890298fd6da44434f3771ac6b5acbd22b16e26").wrap()),
    WIN_EVENTS(new ItemBuilder("/texture/b548dc750ecb146e5c3a4bb890cc63ce2c8dd29b7242aba8f81f7f1ba68e4f6c").wrap()),
    DEFAULT(new ItemBuilder("/texture/6d0f4061bfb767a7f922a6ca7176f7a9b20709bd0512696beb15ea6fa98ca55c").wrap());

    private final ItemStack itemStack;

}
