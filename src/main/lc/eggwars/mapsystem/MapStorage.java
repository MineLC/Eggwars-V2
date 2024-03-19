package lc.eggwars.mapsystem;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.grinderwolf.swm.plugin.SWMPlugin;

import io.netty.util.collection.IntObjectHashMap;
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

    private final Map<World, IntObjectHashMap<ClickableBlock>> clickableBlocks = new HashMap<>();

    private final Map<String, MapData> mapsPerName;
    
    private final SWMPlugin slimePlugin;
    private final SlimeLoader loader;

    MapStorage(SWMPlugin slimePlugin, SlimeLoader loader, Map<String, MapData> mapsPerName) {
        this.slimePlugin = slimePlugin;
        this.loader = loader;
        this.mapsPerName = mapsPerName;
    }

    // Execute this method async
    public CompletableFuture<Void> loadMap(final String worldName) {
        final MapData map = mapsPerName.get(worldName);
        if (map == null) {
            return null;
        }

        return CompletableFuture.runAsync(() -> {
            try {
                slimePlugin.generateWorld(slimePlugin.loadWorld(loader, worldName, false, PROPERTIES));
            } catch (UnknownWorldException | CorruptedWorldException | NewerFormatException | WorldInUseException| IOException e) {
                e.printStackTrace();
            }
        });
    }

    // Execute this method async
    public CompletableFuture<Void> load(final String worldName) {
        return CompletableFuture.runAsync(() -> {
            try {
                SlimeWorld world = slimePlugin.loadWorld(loader, worldName, false, PROPERTIES);
                slimePlugin.generateWorld(world);
            } catch (UnknownWorldException | CorruptedWorldException | NewerFormatException | WorldInUseException | IOException e) {
                e.printStackTrace();
            }
        });
    }


    public void loadClickableBlocks(final World world) {
        final MapData map = mapsPerName.get(world.getName());
        if (map != null) {
            clickableBlocks.put(world, map.getClickableBlocks());
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

    public MapData getMapData(final String worldName) {
        return mapsPerName.get(worldName);
    }

    public Map<String, MapData> getMaps() {
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