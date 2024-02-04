package lc.eggwars.mapsystem;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;

import io.netty.util.collection.IntObjectHashMap;
import lc.eggwars.utils.ClickableBlock;
import lc.eggwars.utils.IntegerUtils;

public final class MapStorage {
    private static MapStorage mapStorage;

    // Used for player interaction listeners
    private final Map<World, IntObjectHashMap<ClickableBlock>> clickableBlocks;
    
    // Used for spawn items fast
    private final GameMap[] maps;

    MapStorage(Map<World, IntObjectHashMap<ClickableBlock>> clickableBlocks, GameMap[] maps) {
        this.clickableBlocks = clickableBlocks;
        this.maps = maps;
    }

    public ClickableBlock getClickableBlock(final World world, Location location) {
        final IntObjectHashMap<ClickableBlock> clickableBlocksInWorld = clickableBlocks.get(world);
        if (clickableBlocksInWorld == null) {
            return null;
        }
        return clickableBlocksInWorld.get(IntegerUtils.combineCords(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }

    public GameMap[] getMaps() {
        return maps;
    }

    public static MapStorage getStorage() {
        return mapStorage;
    }

    final static void update(MapStorage newStorage) {
        mapStorage = newStorage;
    }
}