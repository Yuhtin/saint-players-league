package com.yuhtin.quotes.saint.playersleague.view;

import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.paged.PagedInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.item.enums.DefaultItem;
import com.henryfabio.minecraft.inventoryapi.item.supplier.InventoryItemSupplier;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.henryfabio.minecraft.inventoryapi.viewer.configuration.border.Border;
import com.henryfabio.minecraft.inventoryapi.viewer.impl.paged.PagedViewer;
import com.yuhtin.quotes.saint.playersleague.PlayersLeaguePlugin;
import com.yuhtin.quotes.saint.playersleague.cache.ViewCache;
import com.yuhtin.quotes.saint.playersleague.model.LeagueUser;
import com.yuhtin.quotes.saint.playersleague.util.ItemBuilder;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */
public class RankingView extends PagedInventory {

    private final PlayersLeaguePlugin instance;

    public RankingView(ViewCache viewCache) {
        super("playerleague.ranking", "Ranking", 5 * 9);
        this.instance = viewCache.getPlugin();
    }

    @Override
    protected void configureViewer(PagedViewer viewer) {
        val configuration = viewer.getConfiguration();

        configuration.backInventory("playerleague.main");
        configuration.itemPageLimit(14);
        configuration.border(Border.of(1, 1, 2, 1));
    }

    @Override
    protected void configureInventory(@NotNull Viewer viewer, InventoryEditor editor) {
        viewer.getConfiguration().titleInventory("Ranking");

        editor.setItem(40, DefaultItem.BACK.toInventoryItem(viewer));
    }

    @Override
    protected void update(@NotNull PagedViewer viewer, @NotNull InventoryEditor editor) {
        super.update(viewer, editor);
        configureInventory(viewer, viewer.getEditor());
    }

    @Override
    protected List<InventoryItemSupplier> createPageItems(@NotNull PagedViewer viewer) {
        List<InventoryItemSupplier> items = new ArrayList<>();

        int position = 1;
        for (LeagueUser user : instance.getController().getRanking()) {
            int finalPosition = position;
            items.add(() -> InventoryItem.of(new ItemBuilder(user.getUsername())
                    .name(instance.getConfig().getString("view.ranking.name")
                            .replace("%player%", user.getUsername())
                            .replace("%position%", String.valueOf(finalPosition)))
                    .hideAttributes()
                    .setLore(instance.getConfig().getString("view.ranking.lore")
                            .replace("%rank%", user.getRankPrefix())
                    ).wrap()));


            position++;
        }

        return items;
    }

}
