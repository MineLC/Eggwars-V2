package lc.eggwars.others.pregameitems;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.others.spawn.SpawnStorage;

public final class StartPreGameItems {

    public void load(EggwarsPlugin plugin) {
        final FileConfiguration config = plugin.loadConfig("items/pregame");
        final Map<Material, Inventory> items = new HashMap<>();
        
        final boolean addShopSpawnItem = config.getBoolean("add-shop-spawn-item");
        if (addShopSpawnItem) {
            items.put(SpawnStorage.getStorage().shopItem().item().getType(), SpawnStorage.getStorage().shopInventory().getInventory());
        }

        PregameItemsStorage.update(new PregameItemsStorage(addShopSpawnItem, items));
    }
}