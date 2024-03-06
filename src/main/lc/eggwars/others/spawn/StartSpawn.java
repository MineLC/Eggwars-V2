package lc.eggwars.others.spawn;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.inventory.internal.InventoryCreator;
import lc.eggwars.inventory.internal.InventoryCreator.Item;
import lc.eggwars.inventory.types.SpawnShopInventory;
import lc.eggwars.mapsystem.MapStorage;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class StartSpawn {
    private final EggwarsPlugin plugin;

    public StartSpawn(EggwarsPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadItems() {
        final FileConfiguration config = plugin.loadConfig("items/spawn");
        final InventoryCreator creator = new InventoryCreator(config);

        final Item shopItem = creator.create("shop-item");
        final SpawnShopInventory spawnShopInventory = getSpawnShopInventory();

        final Map<Material, Inventory> items = Map.of(
            shopItem.item().getType(), spawnShopInventory.getInventory()
        );
        SpawnStorage.update(new SpawnStorage(null, shopItem, items, spawnShopInventory));
    }

    public void loadSpawn() {
        final FileConfiguration config = plugin.getConfig();
        final String world = config.getString("spawn.world");
        if (world == null) {
            System.out.println("The spawn world don't exist");
            return;
        }
        final String spawn = config.getString("spawn.cords");
        if (spawn == null) {
            return;
        }
        final CompletableFuture<Void> mapLoaded = MapStorage.getStorage().loadNoGameMap(world);
        if (mapLoaded == null) {
            return;
        }
        mapLoaded.thenAccept((none) -> {
            final World bukkitWorld = Bukkit.getWorld(world);
            final String[] split = spawn.split(",");
            final Location spawnLocation = new Location(
                bukkitWorld,
                Integer.parseInt(split[0]),
                Integer.parseInt(split[1]),
                Integer.parseInt(split[2]));
    
            bukkitWorld.getWorldBorder().setSize(config.getInt("spawn.border"));
            final SpawnStorage oldStorage = SpawnStorage.getStorage();
            SpawnStorage.update(new SpawnStorage(spawnLocation, oldStorage.shopItem(), oldStorage.items(), oldStorage.shopInventory()));
        });
    }

    private SpawnShopInventory getSpawnShopInventory() {
        final FileConfiguration config = plugin.loadConfig("inventories/spawnshop");
        final InventoryCreator creator = new InventoryCreator(config);
        final Inventory inventory = creator.create("spawnshop", "inventory");
        
        final Item skinSelector = creator.create("skin-selector");
        final Item kitSelector = creator.create("kit-selector");
        inventory.setItem(skinSelector.slot(), skinSelector.item());
        inventory.setItem(kitSelector.slot(), kitSelector.item());

        return new SpawnShopInventory(skinSelector, kitSelector, inventory);
    }
}