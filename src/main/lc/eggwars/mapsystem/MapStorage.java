package lc.eggwars.mapsystem;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;

import io.netty.util.collection.IntObjectHashMap;
import lc.eggwars.game.GameMap;
import lc.eggwars.utils.ClickableBlock;
import lc.eggwars.utils.IntegerUtils;

public final class MapStorage {
    private static MapStorage mapStorage;

    private final static SlimePropertyMap PROPERTIES;

    static {
        PROPERTIES = new SlimePropertyMap();
        PROPERTIES.setString(SlimeProperties.DIFFICULTY, "normal");
        PROPERTIES.setBoolean(SlimeProperties.PVP, true);
        PROPERTIES.setBoolean(SlimeProperties.ALLOW_MONSTERS, false);
        PROPERTIES.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
    }

    // Used for player interaction listeners
    private final Map<World, IntObjectHashMap<ClickableBlock>> clickableBlocks = new HashMap<>();

    private final Map<String, GameMap> mapsPerName;

    private final SlimePlugin slimePlugin;
    private final SlimeLoader loader;

    MapStorage(SlimePlugin slimePlugin, SlimeLoader loader, Map<String, GameMap> mapsPerName) {
        this.slimePlugin = slimePlugin;
        this.loader = loader;
        this.mapsPerName = mapsPerName;
    }

    // Execute this method async
    public World load(final String worldName) {
        try {
            final GameMap map = mapsPerName.get(worldName);
            if (map == null) {
                return null;
            }

            final SlimeWorld world = slimePlugin.loadWorld(loader, worldName, false, PROPERTIES);
            slimePlugin.generateWorld(world);
            final World bukkitWorld = Bukkit.getWorld(worldName);
            clickableBlocks.put(bukkitWorld, map.getClickableBlocks());
            bukkitWorld.getWorldBorder().setSize(map.getBorderSize());

            return bukkitWorld;
        } catch (UnknownWorldException | CorruptedWorldException | NewerFormatException | WorldInUseException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void unload(final World world) {
        clickableBlocks.remove(world);
        Bukkit.unloadWorld(world, false);
    }

    public ClickableBlock getClickableBlock(final World world, Location location) {
        final IntObjectHashMap<ClickableBlock> clickableBlocksInWorld = clickableBlocks.get(world);
        if (clickableBlocksInWorld == null) {
            return null;
        }
        return clickableBlocksInWorld.get(IntegerUtils.combineCords(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }

    public GameMap getMap(final String worldName) {
        return mapsPerName.get(worldName);
    }

    public Map<String, GameMap> getMaps() {
        return mapsPerName;
    }

    public SlimeLoader getFileLoader() {
        return loader;
    }

    public static MapStorage getStorage() {
        return mapStorage;
    }

    final static void update(MapStorage newStorage) {
        mapStorage = newStorage;
    }
}