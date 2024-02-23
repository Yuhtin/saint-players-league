package com.yuhtin.quotes.saint.playersleague.view;

import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.simple.SimpleInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.yuhtin.quotes.saint.playersleague.PlayersLeaguePlugin;
import com.yuhtin.quotes.saint.playersleague.cache.ViewCache;
import com.yuhtin.quotes.saint.playersleague.model.LeagueUser;
import com.yuhtin.quotes.saint.playersleague.util.ItemBuilder;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */
public class LeagueView extends SimpleInventory {

    private final ViewCache viewCache;

    public LeagueView(ViewCache viewCache) {
        super("playerleague.main", viewCache.getPlugin().getConfig().getString("view.mainInventoryName"), 3 * 9);
        this.viewCache = viewCache;
    }

    @Override
    protected void configureInventory(Viewer viewer, InventoryEditor editor) {
        PlayersLeaguePlugin instance = viewCache.getPlugin();

        Player player = viewer.getPlayer();
        LeagueUser user = instance.getController().retrieve(player.getName()).getUncachedValue();
        if (user == null) return;

        List<String> lore = new ArrayList<>();
        instance.getConfig().getStringList("view.profile.lore")
                .stream()
                .map(line -> line.replace("%pontos%", String.valueOf(user.getPoints()))
                        .replace("%rank%", user.getRankPrefix()))
                .forEach(lore::add);

        editor.setItem(11, InventoryItem.of(new ItemBuilder(player.getName())
                        .name(instance.getConfig().getString("view.profile.name"))
                        .setLore(lore)
                        .wrap())
                .defaultCallback(callback -> viewCache.getHistoricView().openInventory(
                        player,
                        $ -> $.getPropertyMap().set("playerName", player.getName())
                ))
        );

        editor.setItem(14, InventoryItem.of(new ItemBuilder(instance.getConfig().getString("view.leagueRank.material"))
                        .name(instance.getConfig().getString("view.leagueRank.name"))
                        .setLore(instance.getConfig().getStringList("view.leagueRank.lore"))
                        .wrap())
                .defaultCallback(callback -> viewCache.getRankingView().openInventory(player))
        );

        editor.setItem(15, InventoryItem.of(new ItemBuilder(instance.getConfig().getString("view.leagueHistoric.material"))
                        .name(instance.getConfig().getString("view.leagueHistoric.name"))
                        .setLore(instance.getConfig().getStringList("view.leagueHistoric.lore"))
                        .wrap())
                .defaultCallback(callback -> viewCache.getHistoricView().openInventory(player))
        );
    }
}
