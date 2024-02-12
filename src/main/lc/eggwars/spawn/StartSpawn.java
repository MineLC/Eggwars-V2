package lc.eggwars.spawn;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.inventory.InventoryCreator;
import lc.eggwars.inventory.InventoryStorage;
import lc.eggwars.inventory.PrincipalInventory;
import lc.eggwars.inventory.InventoryCreator.Item;

import java.util.Map;
import java.util.HashMap;

public class StartSpawn {
    private final EggwarsPlugin plugin;

    public StartSpawn(EggwarsPlugin plugin) {
        this.plugin = plugin;
    }

    public Location load() {
        final Location spawn = getSpawn(plugin.getConfig());
        final FileConfiguration config = plugin.loadConfig("spawnitems");
        final InventoryCreator creator = new InventoryCreator(config);

        final Map<Material, PrincipalInventory> items = new HashMap<>();
        final Item shopItem = creator.create("shop-item");
        items.put(shopItem.item().getType(), InventoryStorage.getStorage().getInventories().get("spawnshop")); 
        
        SpawnStorage.update(new SpawnStorage(spawn, shopItem, items));
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
}