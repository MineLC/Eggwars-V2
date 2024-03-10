package lc.eggwars.game.pregameitems;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import lc.eggwars.inventory.internal.InventoryCreator.Item;
import lc.eggwars.others.spawn.SpawnStorage;

public final class PregameItemsStorage {
    private static PregameItemsStorage storage;
    private final boolean addShopSpawnitem;
    private final Item selectTeam;

    PregameItemsStorage(boolean addShopSpawnitem, Item selectTeam) {
        this.addShopSpawnitem = addShopSpawnitem;
        this.selectTeam = selectTeam;
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
        inventory.setItem(selectTeam.slot(), selectTeam.item());
    }

    public Item getSelectTeamItem() {
        return selectTeam;
    }

    static void update(PregameItemsStorage newStorage) {
        storage = newStorage;
    }
}
