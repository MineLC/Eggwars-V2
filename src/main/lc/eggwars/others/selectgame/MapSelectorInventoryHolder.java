package lc.eggwars.others.selectgame;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import lc.eggwars.mapsystem.MapData;
import lc.eggwars.mapsystem.MapStorage;

public class MapSelectorInventoryHolder implements InventoryHolder {

    private static final MapData[] MAPS = MapStorage.getStorage().getMaps();

    @Override
    public Inventory getInventory() {
        return null;
    }

    public MapData getGame(final int clickedSlot) {
        return (clickedSlot >= MAPS.length) ? null : MAPS[clickedSlot];
    }
}
