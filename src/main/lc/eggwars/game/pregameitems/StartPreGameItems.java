package lc.eggwars.game.pregameitems;

import org.bukkit.configuration.file.FileConfiguration;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.inventory.internal.InventoryCreator;

public final class StartPreGameItems {

    public void load(EggwarsPlugin plugin) {
        final FileConfiguration config = plugin.loadConfig("items/pregame");
        final InventoryCreator creator = new InventoryCreator(config);

        final boolean addShopSpawnItem = config.getBoolean("add-shop-spawn-item");

        PregameItemsStorage.update(new PregameItemsStorage(addShopSpawnItem, creator.create("select-team")));
    }
}