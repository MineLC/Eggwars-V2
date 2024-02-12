package lc.eggwars.inventory;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.inventory.InventoryCreator.Item;
import lc.eggwars.inventory.types.SkinShopInventory;
import lc.eggwars.inventory.types.SpawnShopInventory;

import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public final class StartInventories {

    public void load() {
        final Map<String, PrincipalInventory> inventories = new HashMap<>();
        inventories.put("spawnshop", getSpawnShopInventory());
        inventories.put("skinshop", new SkinShopInventory());
        InventoryStorage.update(new InventoryStorage(inventories));
    }

    private SpawnShopInventory getSpawnShopInventory() {
        final FileConfiguration config = EggwarsPlugin.getInstance().loadConfig("spawnshop");
        final InventoryCreator creator = new InventoryCreator(config);
        final Inventory inventory = creator.create("spawnshop", "inventory");
        
        final Item skinSelector = creator.create("skin-selector");
        inventory.setItem(skinSelector.slot(), skinSelector.item());

        return new SpawnShopInventory(creator.create("skin-selector"), inventory);
    }
}