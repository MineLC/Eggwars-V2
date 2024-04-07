package lc.eggwars.others.selectgame;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import lc.eggwars.mapsystem.MapData;

public class MapSelectorInventoryHolder implements InventoryHolder {

    private final MapData[] maps;

    MapSelectorInventoryHolder(MapData[] maps) {
        this.maps = maps;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public MapData getGame(final int clickedSlot) {
        return (clickedSlot >= maps.length) ? null : maps[clickedSlot];
    }
}
