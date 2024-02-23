package com.yuhtin.quotes.saint.playersleague.view;

import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.paged.PagedInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.item.enums.DefaultItem;
import com.henryfabio.minecraft.inventoryapi.item.supplier.InventoryItemSupplier;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.henryfabio.minecraft.inventoryapi.viewer.configuration.border.Border;
import com.henryfabio.minecraft.inventoryapi.viewer.impl.paged.PagedViewer;
import com.henryfabio.minecraft.inventoryapi.viewer.property.ViewerPropertyMap;
import com.yuhtin.quotes.saint.playersleague.PlayersLeaguePlugin;
import com.yuhtin.quotes.saint.playersleague.cache.ViewCache;
import com.yuhtin.quotes.saint.playersleague.model.LeagueEvent;
import com.yuhtin.quotes.saint.playersleague.model.LeagueEventType;
import com.yuhtin.quotes.saint.playersleague.repository.repository.EventRepository;
import com.yuhtin.quotes.saint.playersleague.util.ItemBuilder;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */
public class HistoricView extends PagedInventory {

    private final Map<String, Integer> rankingSorterType = new HashMap<>();
    private final PlayersLeaguePlugin instance;

    public HistoricView(ViewCache viewCache) {
        super("playerleague.historic", "Historico", 5 * 9);
        this.instance = viewCache.getPlugin();
    }

    @Override
    protected void configureViewer(PagedViewer viewer) {
        val configuration = viewer.getConfiguration();

        configuration.backInventory("playerleague.main");
        configuration.itemPageLimit(14);
        configuration.border(Border.of(1, 1, 2, 1));

        ViewerPropertyMap propertyMap = viewer.getPropertyMap();
        String playerName = propertyMap.get("playerName");
        if (playerName != null) {
            configuration.titleInventory("Historico de " + playerName);
        }
    }

    @Override
    protected void configureInventory(Viewer viewer, InventoryEditor editor) {
        editor.setItem(4, sortRankingItem(viewer));
        editor.setItem(40, DefaultItem.BACK.toInventoryItem(viewer));
    }

    @Override
    protected void update(PagedViewer viewer, InventoryEditor editor) {
        super.update(viewer, editor);
        configureInventory(viewer, viewer.getEditor());
    }


    @Override
    protected List<InventoryItemSupplier> createPageItems(PagedViewer viewer) {
        String playerName = viewer.getPlayer().getName();

        EventRepository eventRepository = instance.getController().getEventRepository();
        Set<LeagueEvent> events = eventRepository.groupByPlayer(playerName);

        int filterValue = rankingSorterType.getOrDefault(viewer.getName(), -1);

        List<InventoryItemSupplier> items = new ArrayList<>();
        for (LeagueEvent event : events) {
            LeagueEventType leagueEventType = event.getLeagueEventType();
            if (filterValue != -1 && filterValue != (leagueEventType == null ? 2 : leagueEventType.ordinal() == 3 ? 2 : leagueEventType.ordinal())) continue;

            items.add(() -> {
                ItemStack itemStack = leagueEventType == null
                        ? new ItemBuilder(instance.getConfig().getString("view.defaultEventHead", "/texture/6d0f4061bfb767a7f922a6ca7176f7a9b20709bd0512696beb15ea6fa98ca55c")).wrap()
                        : event.getLeagueEventType().getItemStack();

                return InventoryItem.of(new ItemBuilder(itemStack)
                        .name(instance.getConfig().getString("view.historic.name")
                                .replace("%event%", event.getName())
                                .replace("%id%", event.getId()))
                        .setLore(instance.getConfig().getStringList("view.historic.lore").stream().map(line -> line
                                        .replace("%data%", event.getFormattedDate())
                                        .replace("%pontos%", String.valueOf(event.getPoints())))
                                .collect(Collectors.toList())
                        ).wrap());

            });
        }

        return items;
    }

    private InventoryItem sortRankingItem(Viewer viewer) {
        AtomicInteger currentFilter = new AtomicInteger(rankingSorterType.getOrDefault(viewer.getName(), -1));

        List<String> lore = new ArrayList<>();
        for (String line : instance.getConfig().getStringList("view.sortRanking.lore")) {
            if (line.contains("%info%")) {
                lore.add(getColorByFilter(currentFilter.get(), -1) + " Todos");
                lore.add(getColorByFilter(currentFilter.get(), 0) + " Matar o dragão");
                lore.add(getColorByFilter(currentFilter.get(), 1) + " Eventos de clan");
                lore.add(getColorByFilter(currentFilter.get(), 2) + " Outros eventos");
            } else {
                lore.add(line);
            }
        }

        return InventoryItem.of(new ItemBuilder(Material.valueOf(instance.getConfig().getString("view.sortRanking.material")))
                        .name(instance.getConfig().getString("view.sortRanking.name"))
                        .setLore(lore)
                        .wrap())
                .defaultCallback(event -> {
                    rankingSorterType.put(viewer.getName(), currentFilter.incrementAndGet() > 2 ? -1 : currentFilter.get());
                    event.updateInventory();
                });
    }

    private String getColorByFilter(int currentFilter, int loopFilter) {
        return currentFilter == loopFilter ? " " + instance.getConfig().getString("view.filterColor") + "▶" : "&8";
    }

}
