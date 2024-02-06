package com.yuhtin.quotes.saint.playersleague.ranking;

import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.paged.PagedInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.item.supplier.InventoryItemSupplier;
import com.henryfabio.minecraft.inventoryapi.viewer.configuration.border.Border;
import com.henryfabio.minecraft.inventoryapi.viewer.impl.paged.PagedViewer;
import com.yuhtin.quotes.saint.playersleague.PlayersLeaguePlugin;
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

    public RankingView(PlayersLeaguePlugin instance) {
        super("playersleague.ranking", "Ranking", 5 * 9);
        this.instance = instance;
    }

    @Override
    protected void configureViewer(PagedViewer viewer) {
        val configuration = viewer.getConfiguration();

        configuration.itemPageLimit(14);
        configuration.border(Border.of(1, 1, 2, 1));
    }

    @Override
    protected void update(@NotNull PagedViewer viewer, @NotNull InventoryEditor editor) {
        super.update(viewer, editor);
        configureInventory(viewer, viewer.getEditor());
    }

    @Override
    protected List<InventoryItemSupplier> createPageItems(@NotNull PagedViewer viewer) {
        List<InventoryItemSupplier> items = new ArrayList<>();

        int position = 0;
        for (String user : instance.getController().getRanking()) {
            int finalPosition = position;
            items.add(() -> {
                int points = instance.getController().getPoints(user);
                return InventoryItem.of(new ItemBuilder(user)
                        .name("&a" + user + " &6(#" + finalPosition + ")")
                        .setLore(
                                "&fPontos: &e" + points,
                                "&fRank: &e" + instance.getRankCache().getByPoints(points).getPrefix()
                        ).wrap());
            });

            position++;
        }

        return items;
    }

}
