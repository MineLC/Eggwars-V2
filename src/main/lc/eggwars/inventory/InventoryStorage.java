package lc.eggwars.inventory;

import java.util.Map;

public final class InventoryStorage {
    private static InventoryStorage storage;

    private final Map<String, PrincipalInventory> inventoriesPerName;

    InventoryStorage(Map<String, PrincipalInventory> inventories) {
        this.inventoriesPerName = inventories;
    }

    public Map<String, PrincipalInventory> getInventories() {
        return inventoriesPerName;
    }

    public static InventoryStorage getStorage() {
        return storage;
    }

    static final void update(InventoryStorage newStorage) {
        storage = newStorage;
    }
}
