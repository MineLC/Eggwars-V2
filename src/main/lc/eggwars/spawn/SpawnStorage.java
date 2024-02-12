package lc.eggwars.spawn;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import lc.eggwars.inventory.InventoryCreator.Item;
import lc.eggwars.inventory.PrincipalInventory;

public final class SpawnStorage {
    private static SpawnStorage storage;
    private final Location location;

    private final Item shopItem;
    private final Map<Material, PrincipalInventory> items;

    SpawnStorage(Location location, Item shopItem, Map<Material, PrincipalInventory> items) {
        this.location = location;
        this.shopItem = shopItem;
        this.items = items;
    }

    public Location getLocation() {
        return location;
    }

    public Map<Material, PrincipalInventory> getItems() {
        return items;
    }

    public void setItems(final Player player) {
        final PlayerInventory inventory = player.getInventory();
        inventory.setItem(shopItem.slot(), shopItem.item());
    }

    final static void update(SpawnStorage newStorage) {
        storage = newStorage;
    }

    public static SpawnStorage getStorage() {
        return storage;
    }
}