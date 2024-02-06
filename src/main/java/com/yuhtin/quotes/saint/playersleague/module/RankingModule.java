package com.yuhtin.quotes.saint.playersleague.module;

import com.yuhtin.quotes.saint.playersleague.PlayersLeaguePlugin;
import com.yuhtin.quotes.saint.playersleague.model.LeagueUser;
import com.yuhtin.quotes.saint.playersleague.ranking.RankingView;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.DecentHologramsAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import lombok.AllArgsConstructor;
import me.lucko.helper.Commands;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */
@AllArgsConstructor
public class RankingModule implements TerminableModule {

    private static final List<String> HOLOGRAMS = new ArrayList<>();

    private static final Material[] HEADS = new Material[]{
            Material.DIAMOND_BLOCK, Material.GOLD_BLOCK,
            Material.IRON_BLOCK, Material.STONE,
            Material.OAK_WOOD
    };

    private static final Material[] CHESTPLATE = new Material[]{
            Material.DIAMOND_CHESTPLATE, Material.GOLDEN_CHESTPLATE,
            Material.IRON_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE,
            Material.LEATHER_CHESTPLATE
    };

    private static final Material[] LEGGINGS = new Material[]{
            Material.DIAMOND_LEGGINGS, Material.GOLDEN_LEGGINGS,
            Material.IRON_LEGGINGS, Material.CHAINMAIL_LEGGINGS,
            Material.LEATHER_LEGGINGS
    };

    private static final Material[] BOOTS = new Material[]{
            Material.DIAMOND_BOOTS, Material.GOLDEN_BOOTS,
            Material.IRON_BOOTS, Material.CHAINMAIL_BOOTS,
            Material.LEATHER_BOOTS
    };


    private final PlayersLeaguePlugin instance;

    @Override
    public void setup(@Nonnull TerminableConsumer consumer) {
        Schedulers.sync().runRepeating(this::refreshRanking, 20L, 2 * 60 * 20L).bindWith(consumer);

        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(event -> event.getEntity().hasMetadata("saintplayersleague"))
                .handler(event -> event.setCancelled(true))
                .bindWith(consumer);

        RankingView rankingView = new RankingView(PlayersLeaguePlugin.getInstance()).init();

        Commands.create()
                .assertPlayer()
                .handler(context -> {
                    rankingView.openInventory(context.sender());
                }).registerAndBind(consumer, "ligaplayer");

        Commands.create()
                .assertPlayer()
                .assertPermission("league.admin")
                .assertUsage("<posição>")
                .handler(context -> {
                    String action = context.arg(0).parse(String.class).orElse(null);
                    if (action != null && action.equalsIgnoreCase("reload")) {
                        refreshRanking();

                        context.reply("&aRanking recarregado!");
                        return;
                    }

                    if (!context.arg(1).isPresent()) {
                        context.reply("&cVocê deve especificar o intervalo de tempo! &8(Mensal/Trimestral)");
                        return;
                    }

                    int position = context.arg(0).parseOrFail(Integer.class);
                    if (position < 1 || position > 10) {
                        context.reply("&cPosição inválida! &8(Apenas de 1 à 10)");
                        return;
                    }

                    Player player = context.sender();

                    Location location = player.getLocation();
                    String locationString = location.getWorld().getName() + ","
                            + location.getX() + ","
                            + location.getY() + ","
                            + location.getZ() + ","
                            + location.getYaw() + ","
                            + location.getPitch();

                    instance.getConfig().set("ranking.position." + position, locationString);
                    instance.saveConfig();

                    context.reply("&aVocê definiu a posição #" + position + " do ranking!");
                }).registerAndBind(consumer, "playersranking");
    }

    public void refreshRanking() {
        long start = System.currentTimeMillis();
        instance.getLogger().info("Atualizando ranking...");

        instance.getController().refresh().thenRunSync(() -> {
            long rankingUpdates = System.currentTimeMillis();

            instance.getLogger().info("Ranking atualizado em " + (rankingUpdates - start) + "ms");
            instance.getLogger().info("Atualizando stands e hologramas...");

            clearStands();

            String path = "ranking.position.";

            ConfigurationSection section = instance.getConfig().getConfigurationSection(path);
            if (section == null) {
                instance.getLogger().warning("Ranking position section is null (" + path + ")");
                return;
            }

            for (String key : section.getKeys(false)) {
                int position = Integer.parseInt(key);

                LeagueUser user = instance.getController().getByPosition(position - 1);
                if (user == null) break;

                String locationString = section.getString(key);
                if (locationString == null) continue;

                String[] split = locationString.split(",");
                Location location = new Location(
                        Bukkit.getWorld(split[0]),
                        Double.parseDouble(split[1]),
                        Double.parseDouble(split[2]),
                        Double.parseDouble(split[3]),
                        Float.parseFloat(split[4]),
                        Float.parseFloat(split[5])
                );

                if (location.getWorld() == null) continue;

                updateRanking(user, location, position);
            }

            instance.getLogger().info("Stands e hologramas atualizados em " + (System.currentTimeMillis() - rankingUpdates) + "ms");
        });
    }

    public void clearStands() {
        Bukkit.getWorlds().stream()
                .flatMap(world -> world.getEntities().stream())
                .filter(entity -> entity instanceof ArmorStand)
                .filter(entity -> entity.getCustomName() != null)
                .filter(entity -> entity.getCustomName().equalsIgnoreCase("saintplayersleague"))
                .forEach(Entity::remove);

        HOLOGRAMS.forEach(DHAPI::removeHologram);

        for (Hologram hologram : DecentHologramsAPI.get().getHologramManager().getHolograms()) {
            if (hologram.getName().startsWith("saintplayersleague")) {
                DHAPI.removeHologram(hologram.getName());
            }
        }

        HOLOGRAMS.clear();
    }

    private void updateRanking(LeagueUser user, Location location, int position) {
        createHologram(location.clone().add(0, 3, 0), Arrays.asList(
                "&a" + position + "º Lugar",
                "&e" + user.getUsername(),
                "&e" + user.getPoints() + " pontos"
        ));

        createStand(location, position);
    }

    private void createStand(Location location, int position) {
        ArmorStand stand = location.getWorld().spawn(location, ArmorStand.class);

        stand.setVisible(false);
        stand.setMetadata("saintplayersleague", new FixedMetadataValue(instance, true));
        stand.setCustomNameVisible(false);
        stand.setCustomName("saintplayersleague");
        stand.setGravity(false);
        stand.setArms(true);

        EntityEquipment equipment = stand.getEquipment();
        if (equipment == null) {
            stand.setVisible(true);
            return;
        }

        Material head = HEADS[Math.min(HEADS.length, position) - 1];
        equipment.setHelmet(new ItemStack(head));

        Material chestplate = CHESTPLATE[Math.min(CHESTPLATE.length, position) - 1];
        equipment.setChestplate(new ItemStack(chestplate));

        Material leggings = LEGGINGS[Math.min(LEGGINGS.length, position) - 1];
        equipment.setLeggings(new ItemStack(leggings));

        Material boots = BOOTS[Math.min(BOOTS.length, position) - 1];
        equipment.setBoots(new ItemStack(boots));

        stand.setVisible(true);
    }

    public void createHologram(Location location, List<String> lines) {
        String name = "saintplayersleague-" + new Random().nextInt(10000);

        if (DHAPI.getHologram(name) != null) createHologram(location, lines);
        HOLOGRAMS.add(DHAPI.createHologram(name, location, false, lines).getName());
    }
}
