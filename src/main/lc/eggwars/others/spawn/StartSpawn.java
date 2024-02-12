package lc.eggwars.others.spawn;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import lc.eggwars.EggwarsPlugin;

public class StartSpawn {
    private final EggwarsPlugin plugin;

    public StartSpawn(EggwarsPlugin plugin) {
        this.plugin = plugin;
    }

    public Location load() {
        final FileConfiguration config = plugin.getConfig();
        final World world = Bukkit.getWorld(config.getString("spawn.world"));
        if (world == null) {
            plugin.getLogger().warning("The spawn world don't exist");
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

        SpawnStorage.update(spawnLocation);

        return spawnLocation;
    }
}