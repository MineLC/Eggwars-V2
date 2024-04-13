package lc.eggwars.others.selectgame;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.GameState;
import lc.eggwars.inventory.internal.InventoryCreator;
import lc.eggwars.inventory.internal.InventoryCreator.Item;
import lc.eggwars.inventory.types.SelectMapInventory;
import lc.eggwars.messages.Messages;

public final class StartMapInventories {

    public SelectMapInventory load(final EggwarsPlugin plugin) {
        final FileConfiguration config = plugin.loadConfig("inventories/gameselector");

        final MapInventoryBuilder builder = new MapInventoryBuilder(
            getStateItems(config),
            getLore(config),
            Messages.color(config.getString("games.time")),
            Messages.color(config.getString("games.max-persons"))
        );

        final InventoryCreator creator = new InventoryCreator(config);
        final Inventory selectInventory = creator.create("selectMap", "select-mode-inventory");
        final Item soloMode = creator.create("select-mode-inventory.solo");
        final Item teamMode = creator.create("select-mode-inventory.team");

        selectInventory.setItem(soloMode.slot(), soloMode.item());
        selectInventory.setItem(teamMode.slot(), teamMode.item());

        return new SelectMapInventory(
            builder,
            selectInventory,
            soloMode.slot(),
            teamMode.slot()
        );
    }

    private StateItem[] getStateItems(final FileConfiguration config) {
        final GameState[] gameStates = GameState.values();
        final StateItem[] states = new StateItem[gameStates.length];

        int index = 0;
        for (final GameState state : gameStates) {
            final String stateName = state.name().toLowerCase();
            final String path = "states." + stateName;
            final String suffix = Messages.color(config.getString(path + ".suffix"));
            Material material = Material.getMaterial(config.getString(path + ".item"));
            if (material == null) {
                material = Material.STONE;
            }
            states[index++] = new StateItem(suffix, material);
        }
        return states;
    }

    private List<String> getLore(final FileConfiguration config) {
        final List<String> lore = config.getStringList("games.lore");
        final List<String> newLore = new ArrayList<>(lore.size());

        for (final String line : lore) {
            newLore.add(Messages.color(line));
        }
        return newLore;
    }
}