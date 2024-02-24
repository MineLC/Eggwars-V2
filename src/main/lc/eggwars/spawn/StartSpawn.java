package lc.eggwars.spawn;

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

import java.util.Map;

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

    public Location loadSpawn() {
        final Location spawn = getSpawn(plugin.getConfig());

        final SpawnStorage oldStorage = SpawnStorage.getStorage();
        SpawnStorage.update(new SpawnStorage(spawn, oldStorage.shopItem(), oldStorage.items(), oldStorage.shopInventory()));
        return spawn;
    }

    private Location getSpawn(final FileConfiguration config) {
        final World world = Bukkit.getWorld(config.getString("spawn.world"));
        if (world == null) {
            System.out.println("The spawn world don't exist");
            return null;
        }
        final String spawn = config.getString("spawn.cords");

        if (spawn == null) {
            return null;
        }
        final String[] split = spawn.split(",");
        final Location spawnLocation = new Location(
            world,
            Integer.parseInt(split[0]),
            Integer.parseInt(split[1]),
            Integer.parseInt(split[2]));

        world.getWorldBorder().setSize(config.getInt("spawn.border"));
        return spawnLocation;
    }

    private SpawnShopInventory getSpawnShopInventory() {
        final FileConfiguration config = EggwarsPlugin.getInstance().loadConfig("inventories/spawnshop");
        final InventoryCreator creator = new InventoryCreator(config);
        final Inventory inventory = creator.create("spawnshop", "inventory");
        
        final Item skinSelector = creator.create("skin-selector");
        final Item kitSelector = creator.create("kit-selector");
        inventory.setItem(skinSelector.slot(), skinSelector.item());
        inventory.setItem(kitSelector.slot(), kitSelector.item());

        return new SpawnShopInventory(skinSelector, kitSelector, inventory);
    }
}