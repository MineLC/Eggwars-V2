package lc.eggwars.game.pregame;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.tinylog.Logger;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.inventory.internal.InventoryCreator;
import lc.eggwars.utils.EntityLocation;

public final class StartPreGameData {

    public void loadItems(EggwarsPlugin plugin) {
        final FileConfiguration config = plugin.loadConfig("items/pregame");
        final InventoryCreator creator = new InventoryCreator(config);

        final boolean addShopSpawnItem = config.getBoolean("add-shop-spawn-item");

        PregameStorage.update(new PregameStorage(null, addShopSpawnItem, creator.create("select-team")));
    }

    public void loadMap(final EggwarsPlugin plugin) {
        final FileConfiguration config = plugin.getConfig();
        final String world = config.getString("pregame.world");
        if (world == null) {
            return;
        }
        final String spawn = config.getString("pregame.cords");
        if (spawn == null) {
            return;
        }

        final World bukkitWorld = Bukkit.getWorld(world);
        if (bukkitWorld == null) {
            Logger.warn("can't found the pregame world: " + world);
            return;
        }
        final EntityLocation entityLocation = EntityLocation.create(spawn);
        final Location location = new Location(bukkitWorld, entityLocation.x(), entityLocation.y(), entityLocation.z(), entityLocation.yaw(), entityLocation.pitch());
        bukkitWorld.setSpawnLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        bukkitWorld.getWorldBorder().setSize(config.getInt("pregame.border"));
        final PregameStorage oldStorage = PregameStorage.getStorage();
        PregameStorage.update(new PregameStorage(location, oldStorage.addShopSpawnitem(), oldStorage.selectTeam()));
    }
}