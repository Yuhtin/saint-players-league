package com.yuhtin.quotes.saint.playersleague.util;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static me.lucko.helper.text3.Text.colorize;

public class ItemBuilder {

    private static final ItemStack SKULL_ITEM = new ItemStack(Material.PLAYER_HEAD);

    private final ItemStack item;

    public ItemBuilder(ItemStack item) {
        this.item = item;
    }

    public ItemBuilder(Material type) {
        this(new ItemStack(type));
    }

    public ItemBuilder(Material type, int data) {
        this(new ItemStack(type, 1, (short) data));
    }

    public ItemBuilder(String link) {
        item = SKULL_ITEM.clone();

        if (!link.startsWith("/texture/")) {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setOwner(link);

            item.setItemMeta(meta);
            return;
        }
    }

    public ItemBuilder(Material type, Color color) {
        item = new ItemStack(type);

        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(color);
        item.setItemMeta(meta);
    }

    public ItemBuilder changeItemMeta(Consumer<ItemMeta> consumer) {
        ItemMeta itemMeta = item.getItemMeta();
        consumer.accept(itemMeta);
        item.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder name(String name) {
        return changeItemMeta(it -> it.setDisplayName(colorize(name)));
    }

    public ItemBuilder setLore(String... lore) {
        return changeItemMeta(it -> {
            it.setLore(new ArrayList<>());
            for (String s : lore) {
                it.getLore().add(colorize(s));
            }
        });
    }

    public ItemBuilder setLore(List<String> lore) {
        return changeItemMeta(it -> {
            it.setLore(new ArrayList<>());
            for (String s : lore) {
                it.getLore().add(colorize(s));
            }
        });
    }

    public ItemStack wrap() {
        return item;
    }

    public ItemBuilder hideAttributes() {
        return changeItemMeta(it -> {
            for (ItemFlag value : ItemFlag.values()) {
                it.addItemFlags(value);
            }
        });
    }
}
