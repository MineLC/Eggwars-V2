package lc.eggwars.others.spawn;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import lc.eggwars.inventory.internal.InventoryCreator.Item;
import lc.eggwars.inventory.types.SpawnShopInventory;

public final record SpawnStorage(Location location, Item shopItem, Map<Material, Inventory> items, SpawnShopInventory shopInventory) {
    private static SpawnStorage storage;

    public void setItems(final Player player) {
        final PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setItem(shopItem.slot(), shopItem.item());
    }

    public boolean isInSpawn(final HumanEntity player) {
        return (location == null)
            ? false
            : player.getLocation().getWorld().equals(location.getWorld());
    }

    final static void update(SpawnStorage newStorage) {
        storage = newStorage;
    }

    public static SpawnStorage getStorage() {
        return storage;
    }
}