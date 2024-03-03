package lc.eggwars.others.pregameitems;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import lc.eggwars.others.spawn.SpawnStorage;

public final class PregameItemsStorage {
    private static PregameItemsStorage storage;
    private final boolean addShopSpawnitem;
    private final Map<Material, Inventory> items;

    PregameItemsStorage(boolean addShopSpawnitem, Map<Material, Inventory> items) {
        this.addShopSpawnitem = addShopSpawnitem;
        this.items = items;
    }

    public static PregameItemsStorage getStorage() {
        return storage;
    }

    public void send(final Player player) {
        final PlayerInventory inventory = player.getInventory();
        inventory.clear();
        if (addShopSpawnitem) {
            inventory.setItem(SpawnStorage.getStorage().shopItem().slot(), SpawnStorage.getStorage().shopItem().item());
        }
    }

    public Inventory getInventory(final Material material) {
        return items.get(material);
    }

    static void update(PregameItemsStorage newStorage) {
        storage = newStorage;
    }
}
