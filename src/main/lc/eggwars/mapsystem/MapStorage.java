package lc.eggwars.mapsystem;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import io.netty.util.collection.IntObjectHashMap;

import lc.eggwars.utils.ClickableBlock;
import lc.eggwars.utils.IntegerUtils;

import net.swofty.swm.api.SlimePlugin;
import net.swofty.swm.api.exceptions.CorruptedWorldException;
import net.swofty.swm.api.exceptions.NewerFormatException;
import net.swofty.swm.api.exceptions.UnknownWorldException;
import net.swofty.swm.api.exceptions.WorldInUseException;
import net.swofty.swm.api.loaders.SlimeLoader;
import net.swofty.swm.api.world.properties.SlimeProperties;
import net.swofty.swm.api.world.properties.SlimePropertyMap;

public final class MapStorage {
    private static MapStorage mapStorage;

    private final static SlimePropertyMap PROPERTIES;

    static {
        PROPERTIES = new SlimePropertyMap();
        PROPERTIES.setValue(SlimeProperties.DIFFICULTY, "normal");
        PROPERTIES.setValue(SlimeProperties.PVP, true);
        PROPERTIES.setValue(SlimeProperties.ALLOW_MONSTERS, false);
        PROPERTIES.setValue(SlimeProperties.ALLOW_ANIMALS, false);
    }

    private final Map<World, IntObjectHashMap<ClickableBlock>> clickableBlocks = new HashMap<>();

    private final Map<String, Set<Player>> worldsCurrentlyLoading = new HashMap<>();

    private final Map<String, MapData> mapsPerName;
    
    private final SlimePlugin slimePlugin;
    private final SlimeLoader loader;

    MapStorage(SlimePlugin slimePlugin, SlimeLoader loader, Map<String, MapData> mapsPerName) {
        this.slimePlugin = slimePlugin;
        this.loader = loader;
        this.mapsPerName = mapsPerName;
    }

    // Execute this method async
    public Set<Player> load(final String worldName) {
        try {
            final MapData map = mapsPerName.get(worldName);
            if (map == null) {
                return null;
            }
            final Set<Player> playersWaitingToJoin = new HashSet<>();
            worldsCurrentlyLoading.put(worldName, playersWaitingToJoin);
    
            slimePlugin.generateWorld(slimePlugin.loadWorld(loader, worldName, false, PROPERTIES));
            return playersWaitingToJoin;
        } catch (UnknownWorldException | CorruptedWorldException | NewerFormatException | WorldInUseException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Execute this method async
    public CompletableFuture<Void> loadNoGameMap(final String worldName) {
        try {
            return slimePlugin.generateWorld(slimePlugin.loadWorld(loader, worldName, false, PROPERTIES));
        } catch (UnknownWorldException | CorruptedWorldException | NewerFormatException | WorldInUseException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void loadClickableBlocks(final World world) {
        final MapData map = mapsPerName.get(world.getName());
        if (map != null) {
            clickableBlocks.put(world, map.getClickableBlocks());
        }
    }

    public void unload(final World world) {
        clickableBlocks.remove(world);
        slimePlugin.getSlimeWorlds().get(world.getName()).unloadWorld(false);
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

    public Map<String, Set<Player>> getWorldsCurrentlyLoading() {
        return worldsCurrentlyLoading;
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